package com.wlcb.jpower.module.dbs.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.wlcb.jpower.module.base.annotation.Dict;
import com.wlcb.jpower.module.base.exception.BusinessException;
import com.wlcb.jpower.module.common.node.ForestNodeMerger;
import com.wlcb.jpower.module.common.node.Node;
import com.wlcb.jpower.module.common.node.TreeNode;
import com.wlcb.jpower.module.common.utils.*;
import com.wlcb.jpower.module.dbs.dao.core.dict.mapper.TbCoreDictMapper;
import com.wlcb.jpower.module.dbs.entity.core.city.TbCoreCity;
import com.wlcb.jpower.module.dbs.entity.core.dict.TbCoreDict;
import com.wlcb.jpower.module.mp.support.Condition;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName JpowerServiceImpl
 * @Description TODO
 * @Author 郭丁志
 * @Date 2020-07-03 14:02
 * @Version 1.0
 */
public class JpowerServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    @Autowired
    private TbCoreDictMapper coreDictMapper;

    private Class<T> getTClass(){
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Class<T> cls = (Class) params[1];
        return cls;
    }

    private Map<String,Dict> getDictFiled(Wrapper<T> queryWrapper){

        List<String> selects = null;
        if (StringUtils.isNotBlank(queryWrapper.getSqlSelect())){
            selects = StringUtil.splitTrim(queryWrapper.getSqlSelect(),",");
        }

        //用来返回匹配结果
        Map<String,Dict> map = new HashMap<>();

        Class<?> cls = getTClass();
        List<Field> fields = FieldUtils.getAllFieldsList(cls);
        for (Field field : fields) {
            Dict dictType = field.getAnnotation(Dict.class);
            if (dictType != null && (selects == null || selects.contains(StringUtil.humpToUnderline(field.getName())))){
                map.put(field.getName(),dictType);
            }
        }
        return map;
    }

    /**
     * @Author 郭丁志
     * @Description //TODO 返回所有的字典类型名称 并去重
     * @Date 02:21 2020-07-18
     * @Param [mapFieldDicts]
     * @return java.util.Set<java.lang.String>
     **/
    private Set<String> listDictName(Map<String,Dict> mapFieldDicts){
        Set<String> typeNames = new HashSet<>();
        Collection<Dict> dictNameList = mapFieldDicts.values();
        for (Dict dict : dictNameList) {
            typeNames.add(dict.name());
        }
        return typeNames;
    }

    /**
     * @Author 郭丁志
     * @Description //TODO 查询所有字典
     * @Date 02:21 2020-07-18
     * @Param [dictTypeNames]
     * @return java.util.List<com.wlcb.jpower.module.dbs.entity.core.dict.TbCoreDict>
     **/
    protected List<TbCoreDict> listDict(Collection<String> dictTypeNames){
        List<TbCoreDict> listDict = coreDictMapper.selectList(new QueryWrapper<TbCoreDict>().lambda()
                .select(TbCoreDict::getCode,TbCoreDict::getName,TbCoreDict::getDictTypeCode)
                .in(TbCoreDict::getDictTypeCode,dictTypeNames)
                .eq(TbCoreDict::getStatus,1));
        return listDict;
    }

    /**
     * @Author 郭丁志
     * @Description //TODO 把查询根据字典项分割
     * @Date 02:23 2020-07-18
     * @Param [dicts]
     * @return java.util.Map<java.lang.String,java.util.List<com.wlcb.jpower.module.dbs.entity.core.dict.TbCoreDict>>
     **/
    private Map<String,List<TbCoreDict>> listDictMap(List<TbCoreDict> dicts){
        Map<String,List<TbCoreDict>> dictMap = new HashMap<>();
        if (dicts != null){
            for (TbCoreDict tbCoreDict : dicts) {

                List<TbCoreDict> dictList = dictMap.get(tbCoreDict.getDictTypeCode());
                if (dictList == null){
                    dictList = new ArrayList<>();
                }
                dictList.add(tbCoreDict);
                dictMap.put(tbCoreDict.getDictTypeCode(),dictList);
            }
        }
        return dictMap;
    }

    /**
     * @Author 郭丁志
     * @Description //TODO 给结果MAP新增字典值
     * @Date 17:29 2020-07-24
     * @Param [map, mapFieldDicts, dictMap]
     **/
    private void setMap(Map<String, Object> map,Map<String,Dict> mapFieldDicts,Map<String,List<TbCoreDict>> dictMap){
        for (String filedName : mapFieldDicts.keySet()) {
            Dict dict = mapFieldDicts.get(filedName);
            List<TbCoreDict> dictList = dictMap.get(dict.name());
            if (dictList != null){
                for (TbCoreDict tbCoreDict : dictList) {
                    if (StringUtils.equals(tbCoreDict.getCode(),String.valueOf(map.get(StringUtil.humpToUnderline(filedName))))){
                        map.put(dict.attributes(),tbCoreDict.getName());
                    }
                }
            }
        }
    }

    private void setListMap(List<Map<String, Object>> list,Wrapper<T> queryWrapper){
        if (list != null && list.size()>0){
            Map<String,Dict> mapFieldDicts = getDictFiled(queryWrapper);

            if(mapFieldDicts.size() > 0){

                List<TbCoreDict> dicts = listDict(listDictName(mapFieldDicts));
                Map<String,List<TbCoreDict>> dictMap =  listDictMap(dicts);

                if (dictMap.size() > 0){
                    for (Map<String, Object> map : list) {
                        setMap(map,mapFieldDicts,dictMap);
                    }
                }
            }
        }
    }

    /**
     * 查询所有列表
     * todo 重写mybatis plus方法
     */
    @Override
    public List<Map<String, Object>> listMaps(Wrapper<T> queryWrapper) {
        List<Map<String, Object>> list = super.listMaps(queryWrapper);
        setListMap(list,queryWrapper);
        return list;
    }

    /**
     * 根据 Wrapper，查询一条记录
     * todo 重写mybatis plus 方法
     */
    @Override
    public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
        Map<String, Object> map = super.getMap(queryWrapper);

        if (map != null){
            Map<String,Dict> mapFieldDicts = getDictFiled(queryWrapper);
            if(mapFieldDicts.size() > 0){

                List<TbCoreDict> dicts = listDict(listDictName(mapFieldDicts));
                Map<String,List<TbCoreDict>> dictMap =  listDictMap(dicts);

                if (dictMap.size() > 0){
                    setMap(map,mapFieldDicts,dictMap);
                }
            }
        }

        return map;
    }

    /**
     * @Author 郭丁志
     * @Description //TODO 获取bean的DICT属性并赋值
     * @Date 03:23 2020-07-19
     * @Param [mapFieldDicts, dictMap, t]
     **/
    private void setBean(Map<String,Dict> mapFieldDicts,Map<String,List<TbCoreDict>> dictMap,T t){
        try {
            for (String filedName : mapFieldDicts.keySet()) {
                Dict dict = mapFieldDicts.get(filedName);
                List<TbCoreDict> dictList = dictMap.get(dict.name());

                if (dictList != null){
                    for (TbCoreDict tbCoreDict : dictList) {
                        if (Fc.equalsValue(tbCoreDict.getCode(),ReflectUtil.getFieldValue(t,filedName))){
                            String attributesName = StringUtils.isBlank(dict.attributes())?filedName:dict.attributes();
                            ReflectUtil.setFieldValue(t,attributesName,tbCoreDict.getName());
                        }
                    }
                }
            }
        }catch (Exception e){
            log.error("设置DICT失败，error=",e);
            e.printStackTrace();
        }
    }

    /**
     * @Author 郭丁志
     * @Description //TODO 给单条bean查字典
     * @Date 23:55 2020-07-18
     **/
    private void setBeanDict(T t,Wrapper<T> queryWrapper){
        if (t != null){
            Map<String,Dict> mapFieldDicts = getDictFiled(queryWrapper);
            if(mapFieldDicts.size() > 0){
                List<TbCoreDict> dicts = listDict(listDictName(mapFieldDicts));
                Map<String,List<TbCoreDict>> dictMap =  listDictMap(dicts);

                if (dictMap.size() > 0){
                    setBean(mapFieldDicts,dictMap,t);
                }

            }
        }
    }

    /**
     * @Author 郭丁志
     * @Description //TODO 给多条bean查字典
     * @Date 23:55 2020-07-18
     **/
    private void setListDict(List<T> list,Wrapper<T> queryWrapper){
        if (list != null){
            Map<String,Dict> mapFieldDicts = getDictFiled(queryWrapper);
            if(mapFieldDicts.size() > 0){
                List<TbCoreDict> dicts = listDict(listDictName(mapFieldDicts));
                Map<String,List<TbCoreDict>> dictMap =  listDictMap(dicts);

                if (dictMap.size() > 0){
                    for (T t : list) {
                        setBean(mapFieldDicts,dictMap,t);
                    }
                }

            }
        }
    }

    /**
     * 查询（根据ID）
     * todo 重写mybatisplus方法
     */
    @Override
    public T getById(Serializable id){
        T t = super.getById(id);
        setBeanDict(t,Wrappers.emptyWrapper());
        return t;
    }

    /**
     * 查询（根据ID 批量查询）
     * todo 重写mybatisplus方法
     */
    @Override
    public List<T> listByIds(Collection<? extends Serializable> idList) {
        List<T> list = super.listByIds(idList);
        setListDict(list,Wrappers.emptyWrapper());
        return list;
    }

    @Override
    public List<T> listByMap(Map<String, Object> columnMap) {
        List<T> list = super.listByMap(columnMap);
        setListDict(list,Wrappers.emptyWrapper());
        return list;
    }

    @Override
    public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
        T t = super.getOne(queryWrapper,throwEx);
        setBeanDict(t,queryWrapper);
        return t;
    }

    /**
     * 查询列表
     * todo 重写mybatisplus
     */
    @Override
    public List<T> list(Wrapper<T> queryWrapper) {
        List<T> list = super.list(queryWrapper);
        setListDict(list,queryWrapper);
        return list;
    }

    /**
     * 翻页查询
     *todo 重写mybatisplus
     */
    @Override
    public  <E extends IPage<T>> E page(E page, Wrapper<T> queryWrapper) {
        E e = super.page(page,queryWrapper);
        List<T> list = e.getRecords();
        setListDict(list,queryWrapper);
        e.setRecords(list);
        return e;
    }

    /**
     * 翻页查询
     * todo 重写mybatisplus
     */
    @Override
    public <E extends IPage<Map<String, Object>>> E pageMaps(E page, Wrapper<T> queryWrapper) {
        E e =  super.pageMaps(page,queryWrapper);
        List<Map<String, Object>> list = e.getRecords();
        setListMap(list,queryWrapper);
        e.setRecords(list);
        return e;
    }

    /**
     * 加载树形节点
     *
     * @param treeWrapper
     * @return
     */
    public List<Node> tree(Wrapper<T> treeWrapper) {
        List<Map<String,Object>> list = listMaps(treeWrapper);
        return ForestNodeMerger.merge(list.stream().filter(Objects::nonNull).map(Condition.TreeWrapper::createNode).collect(Collectors.toList()));
    }

    /**
     * @Author 郭丁志
     * @Description //TODO 把查询结果转换成任何类型
     * @Date 21:22 2020-07-30
     * @Param [queryWrapper, mapper]
     * @return java.util.List<V>
     **/
    public  <V> List<V> listToObjs(Wrapper<T> queryWrapper, Function<T, V> mapper) {
        return list(queryWrapper).stream().filter(Objects::nonNull).map(mapper).collect(Collectors.toList());
    }

    /**
     * @Author 郭丁志
     * @Description //TODO 查询树形结构list
     *                  查询字段中必须包含code和parentCode字段，否则无法形成tree列表
     * @Date 21:51 2020-07-30
     * @Param [queryWrapper, clz]
     * @return java.util.List<V>
     **/
    public <V extends Node> List<V> listTree(Wrapper<T> queryWrapper,Class<V> clz) {
        Function<T, V> mapper = new Function<T, V>() {
            @Override
            public V apply(T t) {
                return (V) BeanUtil.copy(t, clz);
            }
        };
        return ForestNodeMerger.merge(listToObjs(queryWrapper,mapper));
    }

    /**
     * 链式查询 普通
     *
     * @return QueryWrapper 的包装类
     *
     * 链式查询暂未支持查询字典
     */
    @Override
    public QueryChainWrapper<T> query() {
        return ChainWrappers.queryChain(getBaseMapper());
    }

    /**
     * 链式查询 lambda 式
     * <p>注意：不支持 Kotlin </p>
     *
     * @return LambdaQueryWrapper 的包装类
     *
     * 链式查询暂未支持查询字典
     */
    @Override
    public LambdaQueryChainWrapper<T> lambdaQuery() {
        return ChainWrappers.lambdaQueryChain(getBaseMapper());
    }

}