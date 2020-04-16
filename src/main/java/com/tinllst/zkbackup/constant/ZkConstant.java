package com.tinllst.zkbackup.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author tinllst
 * @date 9:11 2020/1/13
 */
@PropertySource(value = {"file:zookeeper.properties"})
@Component
@ConfigurationProperties(prefix = "top.zookeeper")
@Data
public class ZkConstant {
    private String serverUrl;
    private String excludeNode = "";
}
