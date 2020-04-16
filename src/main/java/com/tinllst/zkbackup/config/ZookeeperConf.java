package com.tinllst.zkbackup.config;

import com.tinllst.zkbackup.constant.ZkConstant;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author tinllst
 */
@Configuration
public class ZookeeperConf {

    @Resource
    private ZkConstant zkConstant;

    @Bean
    public CuratorFramework getCuratorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                zkConstant.getServerUrl(), 60000, 60000, retryPolicy
        );
        client.start();
        return client;
    }
}