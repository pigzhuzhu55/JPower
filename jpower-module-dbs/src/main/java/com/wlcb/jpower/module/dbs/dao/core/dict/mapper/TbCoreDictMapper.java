package com.wlcb.jpower.module.dbs.dao.core.dict.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wlcb.jpower.module.dbs.entity.core.dict.TbCoreDict;
import com.wlcb.jpower.module.dbs.vo.DictVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName TbCoreParamsDao
 * @Description TODO
 * @Author 郭丁志
 * @Date 2020-07-03 13:30
 * @Version 1.0
 */
@Component("tbCoreDictMapper")
public interface TbCoreDictMapper extends BaseMapper<TbCoreDict> {

    /**
     * @author 郭丁志
     * @Description //TODO 查询字典列表包含是否存在下级列表
     * @date 22:15 2020/8/21 0021
     * @param dict
     * @return java.util.List<com.wlcb.jpower.module.dbs.vo.DictVo>
     */
    List<DictVo> listByType(@Param("dict") TbCoreDict dict);
}
