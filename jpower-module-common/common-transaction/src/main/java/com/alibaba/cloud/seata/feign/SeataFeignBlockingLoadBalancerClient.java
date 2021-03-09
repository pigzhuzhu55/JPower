package com.alibaba.cloud.seata.feign;


import feign.Client;
import feign.Request;
import feign.Response;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.loadbalancer.blocking.client.BlockingLoadBalancerClient;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient;

import java.io.IOException;

/**
 * @ClassName SeataFeignBlockingLoadBalancerClient
 * @Description TODO 重写SeataFeignBlockingLoadBalancerClient以适配最新API
 * @Author goo
 * @Date 2021/3/10 0010 2:06
 * @Version 1.0
 */
public class SeataFeignBlockingLoadBalancerClient extends FeignBlockingLoadBalancerClient {

    public SeataFeignBlockingLoadBalancerClient(Client delegate,
                                                BlockingLoadBalancerClient loadBalancerClient,
                                                SeataFeignObjectWrapper seataFeignObjectWrapper,
                                                LoadBalancerProperties properties,
                                                LoadBalancerClientFactory loadBalancerClientFactory) {
        super((Client) seataFeignObjectWrapper.wrap(delegate), loadBalancerClient, properties, loadBalancerClientFactory);
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        return super.execute(request, options);
    }

}