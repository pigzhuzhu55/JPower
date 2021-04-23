package com.wlcb.jpower.module.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.wlcb.jpower.module.common.utils.Fc;
import com.wlcb.jpower.module.datascope.interceptor.DataScopeInterceptor;
import com.wlcb.jpower.module.mp.CustomSqlInjector;
import com.wlcb.jpower.module.tenant.JpowerTenantProperties;
import lombok.AllArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @ClassName MybatisPlusConfig
 * @Description TODO mybatis配置
 * @Author 郭丁志
 * @Date 2020-07-03 11:47
 * @Version 2.0
 */
@EnableTransactionManagement
@AllArgsConstructor
@MapperScan("com.wlcb.**.dbs.dao.**")
@Configuration(proxyBeanMethods = false)
public class MybatisPlusConfig {

    /**
     * 配置公用字段
     **/
    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(new UpdateRelatedFieldsMetaHandler());
        return globalConfig;
    }

    @Bean
    @ConditionalOnMissingBean({MybatisPlusInterceptor.class})
    public MybatisPlusInterceptor mybatisPlusInterceptor(ObjectProvider<QueryInterceptor[]> queryInterceptors, TenantLineInnerInterceptor tenantLineInnerInterceptor, JpowerTenantProperties tenantProperties) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 分页插件
        JpowerPaginationInterceptor paginationInterceptor = new JpowerPaginationInterceptor();
        QueryInterceptor[] queryInterceptorsArray = queryInterceptors.getIfAvailable();
        if (Fc.isNotEmpty(queryInterceptorsArray)){
            AnnotationAwareOrderComparator.sort(queryInterceptorsArray);
            paginationInterceptor.setQueryInterceptor(queryInterceptorsArray);
        }

        interceptor.addInnerInterceptor(paginationInterceptor);
        // 多租户插件
        if (tenantProperties.getEnable()){
            interceptor.addInnerInterceptor(tenantLineInnerInterceptor);
        }
        return interceptor;
    }

    /**
     * mybatis-plus自3.4.0起采用新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }

    @Bean
    @ConditionalOnMissingBean
    public ISqlInjector sqlInjector() {
        return new CustomSqlInjector();
    }

//    /**
//     * 数据权限插件
//     **/
//    @Order(10)
//    @Bean
//    @ConditionalOnProperty(value = {"jpower.datascope.enable"}, matchIfMissing = true)
//    public DataScopeInterceptor dataScopeInterceptor() {
//        return new DataScopeInterceptor();
//    }

    /**
     * 演示环境
     **/
    @Order(11)
    @Bean
    @ConditionalOnProperty(value = {"jpower.demo.enable"}, matchIfMissing = false)
    public DemoInterceptor demoInterceptor() {
        return new DemoInterceptor();
    }

}
