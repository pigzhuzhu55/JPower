package com.wlcb.jpower.module.common.utils.constants;

/**
 * @ClassName ConstantsUtils
 * @Description TODO
 * @Author 郭丁志
 * @Date 2020-03-28 22:16
 * @Version 1.0
 */
public class ConstantsUtils {

    /** 系统登陆默认密码 **/
    public static final String DEFAULT_PASSWORD = "123456";

    public static final String PROPERTIES_PREFIX = "code:params:";

    public static final String FILE_DES_KEY = "COREFILEENCRYPTKEY20200720";

//    短信APPKEY
    public static final String APP_KEY = "102138";
//    短信密钥
    public static final String APP_SECRET = "48405d1cf063e976";
    public static final String HTTP_SERVER = "http://182.92.7.106:7892";
    public static final String SMS_SEND_URL = HTTP_SERVER + "/api/sms/air/send";
    public static final String SMS_QUERY_BALANCE_URL = HTTP_SERVER + "/api/sms/air/balance";
    public static final String SMS_QUERY_REPORT_URL = HTTP_SERVER + "/api/sms/air/report";
    public static final String SMS_QUERY_MO_URL = HTTP_SERVER + "/api/sms/air/mo";
    public static final String SMS_SECURITY_SEND_URL = HTTP_SERVER + "/api/sms/air/encrptySend";

    /** 用户权限redis Key **/
    public static final String USER_KEY = "user:loginFunction:";
}
