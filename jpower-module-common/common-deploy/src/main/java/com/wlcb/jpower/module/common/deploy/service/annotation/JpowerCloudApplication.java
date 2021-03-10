package com.wlcb.jpower.module.common.deploy.service.annotation;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.lang.annotation.*;

/**
 * Cloud启动注解配置
 * @author goo
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableDiscoveryClient
@SpringBootApplication(excludeName = "org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration")
public @interface JpowerCloudApplication {
}