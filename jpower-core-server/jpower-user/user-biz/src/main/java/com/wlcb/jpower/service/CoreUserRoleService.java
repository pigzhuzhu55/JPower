package com.wlcb.jpower.service;

import com.wlcb.jpower.dbs.entity.TbCoreUserRole;
import com.wlcb.jpower.module.common.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author 郭丁志
 * @Description //TODO 用户角色
 * @date 22:45 2020/5/26 0026
 */
public interface CoreUserRoleService extends BaseService<TbCoreUserRole> {

    /**
     * @author 郭丁志
     * @Description //TODO 查询用户所有角色
     * @date 22:49 2020/5/26 0026
     * @param userId 用户ID
     * @return java.util.List<com.wlcb.jpower.module.dbs.entity.core.role.TbCoreUserRole>
     */
    List<Map<String,Object>> selectUserRoleByUserId(String userId);
}