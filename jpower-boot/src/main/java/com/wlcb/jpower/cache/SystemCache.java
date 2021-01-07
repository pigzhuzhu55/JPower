package com.wlcb.jpower.cache;

import com.wlcb.jpower.dbs.entity.client.TbCoreClient;
import com.wlcb.jpower.dbs.entity.function.TbCoreDataScope;
import com.wlcb.jpower.dbs.entity.function.TbCoreFunction;
import com.wlcb.jpower.dbs.entity.org.TbCoreOrg;
import com.wlcb.jpower.dbs.entity.tenant.TbCoreTenant;
import com.wlcb.jpower.module.common.cache.CacheNames;
import com.wlcb.jpower.module.common.utils.CacheUtil;
import com.wlcb.jpower.module.common.utils.Fc;
import com.wlcb.jpower.module.common.utils.SpringUtil;
import com.wlcb.jpower.module.common.utils.constants.StringPool;
import com.wlcb.jpower.module.mp.support.Condition;
import com.wlcb.jpower.service.client.CoreClientService;
import com.wlcb.jpower.service.client.impl.CoreClientServiceImpl;
import com.wlcb.jpower.service.org.CoreOrgService;
import com.wlcb.jpower.service.org.impl.CoreOrgServiceImpl;
import com.wlcb.jpower.service.role.CoreDataScopeService;
import com.wlcb.jpower.service.role.CoreFunctionService;
import com.wlcb.jpower.service.role.impl.CoreDataScopeServiceImpl;
import com.wlcb.jpower.service.role.impl.CoreFunctionServiceImpl;
import com.wlcb.jpower.service.tenant.TenantService;
import com.wlcb.jpower.service.tenant.impl.TenantServiceImpl;

import java.util.List;

/**
 * @ClassName ParamConfig
 * @Description TODO 获取配置文件参数
 * @Author 郭丁志
 * @Date 2020-05-06 14:55
 * @Version 1.0
 */
public class SystemCache {

    private static CoreOrgService coreOrgService;
    private static CoreClientService coreClientService;
    private static CoreFunctionService coreFunctionService;
    private static TenantService tenantService;
    private static CoreDataScopeService coreDataScopeService;

    static {
        coreOrgService = SpringUtil.getBean(CoreOrgServiceImpl.class);
        coreClientService = SpringUtil.getBean(CoreClientServiceImpl.class);
        coreFunctionService = SpringUtil.getBean(CoreFunctionServiceImpl.class);
        tenantService = SpringUtil.getBean(TenantServiceImpl.class);
        coreDataScopeService = SpringUtil.getBean(CoreDataScopeServiceImpl.class);
    }

    /**
     * @Author 郭丁志
     * @Description //TODO 获取部门名称
     * @Date 15:47 2020-05-06
     **/
    public static String getOrgName(String orgId){
        TbCoreOrg org = getOrg(orgId);
        if (Fc.isNull(org)){
            return StringPool.EMPTY;
        }
        return org.getName();
    }

    /**
     * @Author 郭丁志
     * @Description //TODO 获取部门
     * @Date 15:47 2020-05-06
     **/
    public static TbCoreOrg getOrg(String orgId){
        return CacheUtil.get(CacheNames.SYSTEM_REDIS_CACHE,CacheNames.SYSTEM_ORG_ID_KEY,orgId,() -> {
            TbCoreOrg org = coreOrgService.getById(orgId);
            return org;
        });
    }

    /**
     * @Author 郭丁志
     * @Description //TODO 根据部门ID获取下级所有ID
     * @Date 15:47 2020-05-06
     **/
    public static List<String> getChildIdOrgById(String orgId) {
        return CacheUtil.get(CacheNames.SYSTEM_REDIS_CACHE,CacheNames.SYSTEM_ORG_PARENT_KEY,orgId,() -> {
            List<String> responseData = coreOrgService.queryChildById(orgId);
            return responseData;
        });
    }

    /**
     * @Author 郭丁志
     * @Description //TODO 获取客户端信息
     * @Date 15:47 2020-05-06
     **/
    public static TbCoreClient getClientByClientCode(String clientCode) {
        return CacheUtil.get(CacheNames.SYSTEM_REDIS_CACHE,CacheNames.SYSTEM_CLIENT_KEY,clientCode,() -> {
            TbCoreClient responseData = coreClientService.loadClientByClientCode(clientCode);
            return responseData;
        });
    }

    /**
     * @author 郭丁志
     * @Description //TODO
     * @date 23:51 2020/10/17 0017
     * @param roleIds 角色ID
     */
    public static List<Object> getUrlsByRoleIds(List<String> roleIds) {
        return CacheUtil.get(CacheNames.SYSTEM_REDIS_CACHE,CacheNames.SYSTEM_URL_ROLES_KEY,roleIds,() -> {
            List<Object> responseData = coreFunctionService.getUrlsByRoleIds(roleIds);
            return responseData;
        });
    }

    /**
     * @author 郭丁志
     * @Description //TODO 获取租户信息
     * @date 17:38 2020/10/25 0025
     * @param tenantCode 租户编码
     * @return com.wlcb.jpower.dbs.entity.tenant.TbCoreTenant
     */
    public static TbCoreTenant getTenantByCode(String tenantCode) {
        return CacheUtil.get(CacheNames.SYSTEM_REDIS_CACHE,CacheNames.SYSTEM_TENANT_CODE_KEY,tenantCode,() -> {
            TbCoreTenant responseData = tenantService.getOne(Condition.<TbCoreTenant>getQueryWrapper().lambda().eq(TbCoreTenant::getTenantCode,tenantCode));
            return responseData;
        });
    }

    /**
     * 通过角色ID获取所有菜单
     *
     * @author 郭丁志
     * @date 23:28 2020/11/5 0005
     * @param roleIds 角色ID
     */
    public static List<TbCoreFunction> getMenuListByRole(List<String> roleIds) {
        return CacheUtil.get(CacheNames.SYSTEM_REDIS_CACHE,CacheNames.SYSTEM_MENU_ROLES_KEY,roleIds,() -> {
            List<TbCoreFunction> responseData = coreFunctionService.getMenuListByRole(roleIds);
            return responseData;
        });
    }

    /**
     * 查询可所有角色执行得数据权限
     *
     * @author 郭丁志
     * @date 23:31 2020/11/5 0005
     * @return java.util.List<com.wlcb.jpower.dbs.entity.function.TbCoreDataScope>
     */
    public static List<TbCoreDataScope> getAllRoleDataScope() {
        return CacheUtil.get(CacheNames.SYSTEM_REDIS_CACHE,CacheNames.SYSTEM_DATASCOPE_ALLROLES_KEY,"all",() -> {
            List<TbCoreDataScope> responseData = coreDataScopeService.getAllRoleDataScope();
            return responseData;
        });
    }

    /**
     * 根据角色ID获取数据权限
     *
     * @author 郭丁志
     * @date 23:38 2020/11/5 0005
     * @param roleIds  角色ID
     * @return java.util.List<com.wlcb.jpower.dbs.entity.function.TbCoreDataScope>
     */
    public static List<TbCoreDataScope> getDataScopeByRole(List<String> roleIds) {
        return CacheUtil.get(CacheNames.SYSTEM_REDIS_CACHE,CacheNames.SYSTEM_DATASCOPE_ROLES_KEY,roleIds,() -> {
            List<TbCoreDataScope> responseData = coreDataScopeService.getDataScopeByRole(roleIds);
            return responseData;
        });
    }
}