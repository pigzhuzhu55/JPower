package com.wlcb.wlj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @ClassName SpringBootStartApplication
 * @Description TODO 企业法人服务入口
 * @Author 郭丁志
 * @Date 2020-02-24 18:41
 * @Version 1.0
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = "com.wlcb.wlj.module.dbs.dao")
public class CorporateStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorporateStartApplication.class, args);
    }

}
