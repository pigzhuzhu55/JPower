package com.wlcb.jpower.feign.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

import static org.springframework.cloud.openfeign.support.FeignHttpClientProperties.*;

/**
 * @author mr.g
 * @date 2021-05-21 15:30
 */
@Data
@ConfigurationProperties(prefix = "jpower.feign.http")
public class FeignHttpProperties {

    /** 是否打印日志 **/
    private boolean log = true;

    /** 线程池最大连接数 **/
    private int maxConnections = DEFAULT_MAX_CONNECTIONS;
    /** 线程存活时间(默认单位秒) **/
    private long timeToLive = DEFAULT_TIME_TO_LIVE;
    /** 线程存活时间单位 **/
    private TimeUnit timeToLiveUnit = DEFAULT_TIME_TO_LIVE_UNIT;

    /** 是否启用重定向 **/
    private boolean followRedirects = DEFAULT_FOLLOW_REDIRECTS;
    /** 连接超时时间(单位毫秒) **/
    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    /** 是否启用SSL验证 **/
    private boolean disableSslValidation = DEFAULT_DISABLE_SSL_VALIDATION;
    /** 是否开启重试 **/
    private boolean retryOnConnectionFailure = false;
    /** 读取超时(单位毫秒) **/
    private int readTimeout = 30_000;
    /** 写入超时(单位毫秒) **/
    private int writeTimeout = 30_000;
    /** 整个流程耗费的超时时间(单位毫秒) **/
    private int callTimeout = readTimeout+writeTimeout+10_000;

}
