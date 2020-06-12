package com.wlcb.jpower.module.common.service.core.user.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.wlcb.jpower.module.base.vo.ResponseData;
import com.wlcb.jpower.module.common.service.core.user.CoreUserService;
import com.wlcb.jpower.module.common.service.redis.RedisUtils;
import com.wlcb.jpower.module.common.utils.JWTUtils;
import com.wlcb.jpower.module.common.utils.ReturnJsonUtil;
import com.wlcb.jpower.module.common.utils.UUIDUtil;
import com.wlcb.jpower.module.common.utils.constants.ConstantsEnum;
import com.wlcb.jpower.module.common.utils.param.ParamConfig;
import com.wlcb.jpower.module.dbs.dao.core.user.TbCoreFunctionMapper;
import com.wlcb.jpower.module.dbs.dao.core.user.TbCoreRoleFunctionMapper;
import com.wlcb.jpower.module.dbs.dao.core.user.TbCoreUserMapper;
import com.wlcb.jpower.module.dbs.dao.core.user.TbCoreUserRoleMapper;
import com.wlcb.jpower.module.dbs.entity.core.function.TbCoreFunction;
import com.wlcb.jpower.module.dbs.entity.core.role.TbCoreUserRole;
import com.wlcb.jpower.module.dbs.entity.core.user.TbCoreUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author mr.gmac
 */
@Slf4j
@Service("coreUserService")
public class CoreUserServiceImpl implements CoreUserService {

    @Autowired
    private TbCoreUserMapper coreUserMapper;
    @Autowired
    private TbCoreUserRoleMapper coreUserRoleMapper;
    @Autowired
    private TbCoreRoleFunctionMapper coreRoleFunctionMapper;
    @Autowired
    private TbCoreFunctionMapper coreFunctionMapper;
    @Autowired
    private RedisUtils redisUtils;

    private final String tokenExpired = "tokenExpired";
    private final Long tokenExpiredDefVal = 2400000L;

    /** 用户权限redis Key **/
    private final String USER_KEY = "user:loginFunction:";

    @Override
    public List<TbCoreUser> list(TbCoreUser coreUser) {

        EntityWrapper wrapper = new EntityWrapper<TbCoreUser>();

        if (StringUtils.isNotBlank(coreUser.getOrgId())){
            wrapper.eq("org_id",coreUser.getOrgId());
        }

        if (StringUtils.isNotBlank(coreUser.getLoginId())){
            wrapper.eq("login_id",coreUser.getLoginId());
        }

        if (StringUtils.isNotBlank(coreUser.getUserName())){
            wrapper.eq("user_name",coreUser.getUserName());
        }

        if (StringUtils.isNotBlank(coreUser.getIdNo())){
            wrapper.like("id_no",coreUser.getIdNo());
        }

        if (coreUser.getUserType() != null){
            wrapper.eq("user_type",coreUser.getUserType());
        }

        if (StringUtils.isNotBlank(coreUser.getTelephone())){
            wrapper.like("telephone",coreUser.getTelephone());
        }

        if (coreUser.getActivationStatus() != null){
            wrapper.eq("activation_status",coreUser.getActivationStatus());
        }

        if (coreUser.getStatus() != null){
            wrapper.eq("status",coreUser.getStatus());
        }

        wrapper.orderBy("create_time",false);

        return coreUserMapper.selectList(wrapper);
    }

    @Override
    public Integer add(TbCoreUser coreUser) {
        if (coreUser.getActivationStatus() == null){
            Integer isActivation = ParamConfig.getInt("isActivation");

            coreUser.setActivationStatus(isActivation);
        }

        if (!ConstantsEnum.ACTIVATION_STATUS.ACTIVATION_YES.getValue().equals(coreUser.getActivationStatus())){
            coreUser.setActivationCode(UUIDUtil.create10UUidNum());
            coreUser.setActivationStatus(ConstantsEnum.ACTIVATION_STATUS.ACTIVATION_NO.getValue());
        }

        coreUser.setUpdateUser(coreUser.getCreateUser());
        return coreUserMapper.insert(coreUser);
    }

    @Override
    public Integer delete(String ids) {
        List<String> list = new ArrayList<>(Arrays.asList(ids.split(",")));
        return coreUserMapper.deleteBatchIds(list);
    }

    @Override
    public Integer update(TbCoreUser coreUser) {
        return coreUserMapper.updateById(coreUser);
    }

    @Override
    public TbCoreUser selectUserLoginId(String loginId) {
        TbCoreUser coreUser = new TbCoreUser();
        coreUser.setLoginId(loginId);
        return coreUserMapper.selectOne(coreUser);
    }

    @Override
    public TbCoreUser selectUserById(String id) {
        return coreUserMapper.selectAllById(id);
    }

    @Override
    public Integer updateUserPassword(String ids, String pass) {
        EntityWrapper wrapper = new EntityWrapper<TbCoreUser>();
        wrapper.in("id",ids);
        return coreUserMapper.updateForSet("password = "+pass,wrapper);
    }

    @Override
    public Integer insterBatch(List<TbCoreUser> list) {
        return coreUserMapper.insertList(list);
    }

    @Override
    public Integer updateUsersRole(String userIds, String roleIds) {
        String[] rIds = roleIds.split(",");
        String[] uIds = userIds.split(",");
        List<TbCoreUserRole> userRoles = new ArrayList<>();
        for (String rId : rIds) {
            for (String userId : uIds) {
                TbCoreUserRole userRole = new TbCoreUserRole();
                userRole.setId(UUIDUtil.getUUID());
                userRole.setUserId(userId);
                userRole.setRoleId(rId);
                userRoles.add(userRole);
            }
        }

        //先删除用户原有角色
        EntityWrapper wrapper = new EntityWrapper<TbCoreUserRole>();
        wrapper.in("user_id",uIds);
        coreUserRoleMapper.delete(wrapper);

        if (userRoles.size() > 0){
            Integer count = coreUserRoleMapper.insertList(userRoles);
            return count;
        }
        return 1;
    }

    @Override
    public ResponseData createToken(TbCoreUser user) {
        try {
            List<String> roleIdList = coreUserRoleMapper.selectRoleIdByUserId(user.getId());
            List<String> functionIdList = coreRoleFunctionMapper.selectFunctionIdInRoleIds(roleIdList);
            List<TbCoreFunction> list = coreFunctionMapper.selectBatchIds(functionIdList);

            JSONObject json = JSON.parseObject(JSON.toJSONString(user));
            json.put("menu",list);

            Map<String, Object> payload = new HashMap<String, Object>();
            payload.put("userId", user.getId());
            String token = JWTUtils.createJWT(JSON.toJSONString(user),payload,ParamConfig.getLong(tokenExpired,tokenExpiredDefVal));

            if (StringUtils.isBlank(token)){
                log.error("token生成错误，token={}",token);
            }

            json.put("token",token);

            redisUtils.set(USER_KEY+user.getId(),list,ParamConfig.getLong(tokenExpired,tokenExpiredDefVal), TimeUnit.MILLISECONDS);

            log.info("后台用户登录成功，用户名={},id={},token={}",user.getLoginId(),user.getId(),token);

            return ReturnJsonUtil.printJson(200,"登录成功",json,false);
        }catch (Exception e){
            log.error("登录出错：{}",e.getMessage());
            return ReturnJsonUtil.printJson(500,"登录失败",false);
        }
    }
}