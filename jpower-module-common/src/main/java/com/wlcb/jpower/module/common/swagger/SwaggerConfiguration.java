package com.wlcb.jpower.module.common.swagger;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @ClassName SwaggerConfiguration
 * @Description TODO Swagger配置
 * @Author 郭丁志
 * @Date 2020-08-12 11:23
 * @Version 1.0
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@Profile({"dev", "test"})
@EnableConfigurationProperties({SwaggerProperties.class})
@Import({BeanValidatorPluginsConfiguration.class})
public class SwaggerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SwaggerProperties getSwaggerProperties(){
        return new SwaggerProperties();
    }

    @Bean
    public Docket createRestApi() {

        SwaggerProperties properties = getSwaggerProperties();

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo(properties))
//                .groupName(properties.getGroupName())
                .select()
                .apis(SwaggerConfigUtil.basePackage(properties.getBasePackage()))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes(getSwaggerProperties()))
                .securityContexts(securityContexts())
                .pathMapping("/");
    }

    private List<? extends SecurityScheme> securitySchemes(SwaggerProperties swaggerProperties) {
        List<ApiKey> list = new ArrayList<>();
        swaggerProperties.getAuthorization().forEach(authorization -> {
            list.add(new ApiKey(authorization.getName(),authorization.getName(),authorization.getType()));
        });
        return list;
    }

    private List<SecurityContext> securityContexts() {
        return newArrayList(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("^(?!auth).*$"))
                        .build()
        );
    }

    private List<SecurityReference> defaultAuth() {
        List<SecurityReference> securityReferences = new ArrayList<>();
        getSwaggerProperties().getAuthorization().forEach(authorization -> {
            securityReferences.add(new SecurityReference(authorization.getName(), authorization.getAuthorizationScopes().toArray(new AuthorizationScope[authorization.getAuthorizationScopes().size()])));
        });
        return securityReferences;
    }

    private ApiInfo apiInfo(SwaggerProperties properties) {
        return new ApiInfoBuilder()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .license(properties.getLicense())
                .licenseUrl(properties.getLicenseUrl())
                .termsOfServiceUrl(properties.getTermsOfServiceUrl())
                .contact(properties.getContact())
                .version(properties.getVersion())
                .build();
    }

}
