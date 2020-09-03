package com.wlcb.jpower;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @ClassName SpringBootStartApplication
 * @Description TODO 用户管理入口
 * @Author 郭丁志
 * @Date 2020-02-24 18:41
 * @Version 1.0
 */
@SpringBootApplication
@EnableTransactionManagement
public class BootStartApplication {

    public static void main(String[] args) {
//        JpowerApplication.run(AppConstant.JPOWER, BootStartApplication.class,args);
        SpringApplication.run(BootStartApplication.class,args);
    }

}