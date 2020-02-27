package com.wlcb.module.dbs.dao.corporate;

import com.wlcb.module.dbs.dao.BaseMapper;
import com.wlcb.module.dbs.entity.corporate.TblCsrrgCorporate;
import com.wlcb.module.dbs.entity.corporate.TblCsrrgRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author mr.gmac
 */
@Component("corporateMapper")
public interface CorporateMapper extends BaseMapper<TblCsrrgCorporate> {

    List<Map<String,String>> selectName(String name);

    TblCsrrgCorporate selectDetailByLegal(TblCsrrgRecord record);
}
