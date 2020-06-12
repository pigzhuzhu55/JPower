package com.wlcb.jpower.module.common.service.core.params.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.wlcb.jpower.module.common.service.core.params.CoreParamService;
import com.wlcb.jpower.module.common.service.redis.RedisUtils;
import com.wlcb.jpower.module.common.utils.constants.ConstantsUtils;
import com.wlcb.jpower.module.dbs.dao.core.params.TbCoreParamsMapper;
import com.wlcb.jpower.module.dbs.entity.core.params.TbCoreParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mr.gmac
 */
@Service("coreParamService")
public class CoreParamServiceImpl implements CoreParamService {

    @Autowired
    private TbCoreParamsMapper paramsMapper;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String selectByCode(String code) {
        return paramsMapper.selectByCode(code);
    }

    @Override
    public List<TbCoreParam> list(TbCoreParam coreParam) {

        EntityWrapper wrapper = new EntityWrapper<TbCoreParam>();

        if (StringUtils.isNotBlank(coreParam.getCode())){
            wrapper.eq("code",coreParam.getCode());
        }

        if (StringUtils.isNotBlank(coreParam.getName())){
            wrapper.eq("name",coreParam.getName());
        }

        if (StringUtils.isNotBlank(coreParam.getName())){
            wrapper.like("value",coreParam.getValue());
        }

        wrapper.orderBy("create_time",false);

        return paramsMapper.selectList(wrapper);
    }

    @Override
    public Integer delete(String id) {

        TbCoreParam coreParam = paramsMapper.selectById(id);

        Integer c = paramsMapper.deleteById(id);

        if (c > 0){
            redisUtils.remove(coreParam.getCode());
        }

        return c;
    }

    @Override
    public Integer update(TbCoreParam coreParam) {
        return paramsMapper.updateById(coreParam);
    }

    @Override
    public Integer add(TbCoreParam coreParam) {
        coreParam.setUpdateUser(coreParam.getCreateUser());
        return paramsMapper.insert(coreParam);
    }

    @Override
    public void effectAll() {
        List<TbCoreParam> params = list(new TbCoreParam());

        for (TbCoreParam param : params) {
            if (StringUtils.isNotBlank(param.getValue())){
                redisUtils.set(ConstantsUtils.PROPERTIES_PREFIX+param.getCode(),param.getValue());
            }
        }
    }

}