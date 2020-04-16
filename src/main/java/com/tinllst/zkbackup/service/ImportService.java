package com.tinllst.zkbackup.service;

import com.alibaba.fastjson.JSON;
import com.tinllst.zkbackup.constant.Constants;
import com.tinllst.zkbackup.domain.NodeDto;
import com.tinllst.zkbackup.utils.ThrowExceptionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author tinllst
 * @date 15:57 2020/1/9
 */
@Service
public class ImportService {

    @Resource
    private CuratorFramework client;

    public void importFile(String filePath) {
        try {
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
                String json = reader.lines()
                        .filter(Objects::nonNull)
                        .filter(line -> line.trim().length() > 0 && !line.trim().startsWith("#"))
                        .collect(Collectors.joining(""));
                List<NodeDto> beanList = JSON.parseArray(json, NodeDto.class);
                beanList.parallelStream()
                        .forEach(line ->
                                ThrowExceptionUtils.doThrow(() -> {
                                    importData(line);
                                    return null;
                                })
                        );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void importData(NodeDto bean) throws Exception {

        String value = bean.getValue();

        if (bean.getNodePath().startsWith(Constants.ZK_SYSTEM_NODE)) {
            return;
        }

        if (nodeExists(bean.getNodePath())) {
            client.setData().forPath(bean.getNodePath(), string2Byte(value));
        } else {
            client.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT)
                    .forPath(bean.getNodePath(), string2Byte(value));
        }
    }

    public boolean nodeExists(String nodeFullPath) throws Exception {
        return null != client.checkExists().forPath(nodeFullPath);
    }

    private byte[] string2Byte(String value) {
        return null == value ? new byte[]{} : value.getBytes(StandardCharsets.UTF_8);
    }

}