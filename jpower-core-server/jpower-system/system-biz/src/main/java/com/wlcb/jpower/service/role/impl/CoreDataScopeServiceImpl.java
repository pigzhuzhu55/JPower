package com.wlcb.jpower.service.role.impl;

import com.wlcb.jpower.dbs.dao.role.TbCoreDataScopeDao;
import com.wlcb.jpower.dbs.dao.role.TbCoreRoleDataDao;
import com.wlcb.jpower.dbs.dao.role.mapper.TbCoreDataScopeMapper;
import com.wlcb.jpower.dbs.entity.function.TbCoreDataScope;
import com.wlcb.jpower.dbs.entity.role.TbCoreRoleData;
import com.wlcb.jpower.module.base.enums.JpowerError;
import com.wlcb.jpower.module.base.exception.JpowerAssert;
import com.wlcb.jpower.module.common.service.impl.BaseServiceImpl;
import com.wlcb.jpower.module.common.utils.Fc;
import com.wlcb.jpower.module.mp.support.Condition;
import com.wlcb.jpower.service.role.CoreDataScopeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ding
 * @description 数据权限业务
 * @date 2020-11-03 15:02
 */
@Service
@AllArgsConstructor
public class CoreDataScopeServiceImpl extends BaseServiceImpl<TbCoreDataScopeMapper, TbCoreDataScope> implements CoreDataScopeService {

    private TbCoreDataScopeDao dataScopeDao;
    private TbCoreRoleDataDao roleDataDao;

    @Override
    public boolean save(TbCoreDataScope dataScope){
        JpowerAssert.geZero(dataScopeDao.count(Condition.<TbCoreDataScope>getQueryWrapper().lambda()
                .eq(TbCoreDataScope::getScopeCode,dataScope.getScopeCode())), JpowerError.BUSINESS,"编号已经存在");

        return dataScopeDao.save(dataScope);
    }

    @Override
    public boolean roleDataScope(String roleId, String dataIds) {

        roleDataDao.removeReal(Condition.<TbCoreRoleData>getQueryWrapper().lambda()
                                .eq(TbCoreRoleData::getRoleId,roleId));
        List<String> dataScopeIds = Fc.toStrList(dataIds);
        if (dataScopeIds.size() > 0){
            List<TbCoreRoleData> list = new ArrayList<>();
            dataScopeIds.forEach(dataId -> {
                TbCoreRoleData roleData = new TbCoreRoleData();
                roleData.setDataId(dataId);
                roleData.setRoleId(roleId);
                list.add(roleData);
            });
            return roleDataDao.saveBatch(list);
        }
        return true;
    }

}
