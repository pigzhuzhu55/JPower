package com.wlcb.ylth.module.common.utils.constants;

/**
 * @ClassName ConstantsUtils
 * @Description TODO
 * @Author 郭丁志
 * @Date 2020-03-28 22:16
 * @Version 1.0
 */
public class ConstantsUtils {

//    短信APPKEY
    public static final String APP_KEY = "102138";
//    短信密钥
    public static final String APP_SECRET = "48405d1cf063e976";
    public static final String HTTP_SERVER = "http://47.95.239.60:7802";
    public static final String SMS_SEND_URL = HTTP_SERVER + "/api/sms/air/send";
    public static final String SMS_QUERY_BALANCE_URL = HTTP_SERVER + "/api/sms/air/balance";
    public static final String SMS_QUERY_REPORT_URL = HTTP_SERVER + "/api/sms/air/report";
    public static final String SMS_QUERY_MO_URL = HTTP_SERVER + "/api/sms/air/mo";
    public static final String SMS_SECURITY_SEND_URL = HTTP_SERVER + "/api/sms/air/encrptySend";
}
