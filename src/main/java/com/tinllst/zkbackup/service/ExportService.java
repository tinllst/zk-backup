package com.tinllst.zkbackup.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.tinllst.zkbackup.constant.Constants;
import com.tinllst.zkbackup.constant.ZkConstant;
import com.tinllst.zkbackup.domain.NodeDto;
import com.tinllst.zkbackup.utils.ThrowExceptionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author tinllst
 * @date 15:56 2020/1/9
 */
@Service
public class ExportService {

    @Resource
    private CuratorFramework client;
    @Resource
    private ZkConstant zkConstant;

    public final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    public final static DateTimeFormatter FORMATTER_2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void exportFile(String nodePath, String filePath) {
        List<String> excludeString = Stream.of(
                Optional.ofNullable(zkConstant.getExcludeNode()).orElse("").split(",")
        ).filter(str -> !StringUtils.isEmpty(str)).collect(Collectors.toList());
        excludeString.add(Constants.ZK_SYSTEM_NODE);
        System.out.println("excludeNodeList:" + excludeString);
        try {
            LocalDateTime now = LocalDateTime.now();
            String fileName = "zkBackup_" + FORMATTER.format(now);
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            List<NodeDto> nodeDtoList = exportTree(nodePath, excludeString);
            if (!nodeDtoList.isEmpty()) {
                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath, fileName))) {
                    String string = JSON.toJSONString(nodeDtoList, SerializerFeature.PrettyFormat,
                            SerializerFeature.WriteMapNullValue);
                    writer.write("#zk config created on '" + FORMATTER_2.format(now) + "'"
                            + " from nodePath='" + nodePath + "' to filePath='" + filePath + "'");
                    writer.newLine();
                    writer.write(string);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<NodeDto> exportTree(String nodePath, List<String> excludeString) throws Exception {
        List<NodeDto> leaves = Collections.synchronizedList(new ArrayList<>());
        exportTreeRecursion(leaves, nodePath, excludeString);
        leaves.sort(Comparator.comparing(NodeDto::getNodePath));
        return leaves;
    }

    private void exportTreeRecursion(List<NodeDto> entries, String nodePath, List<String> excludeString) throws Exception {
        List<String> children = Optional.ofNullable(client.getChildren().forPath(nodePath)).orElse(new ArrayList<>());
        boolean isFolder = !children.isEmpty();
        // 排除列表，只过滤path
        if (isFolder && excludeString.stream().anyMatch(excl -> nodePath.toUpperCase().contains(excl.toUpperCase()))) {
            return;
        }
        if (isFolder) {
            children.parallelStream()
                    .forEach(child ->
                            ThrowExceptionUtils.doThrow(() -> {
                                String childPath = getNodePath(nodePath, child);
                                exportTreeRecursion(entries, childPath, excludeString);
                                return null;
                            }));
        } else {
            entries.add(this.getNodeValue(nodePath));
        }
    }

    private NodeDto getNodeValue(String fullNodePath) throws Exception {
        String value = byte2String(client.getData().forPath(fullNodePath));
        return new NodeDto(fullNodePath, value);
    }

    private String getNodePath(String path, String name) {
        return path + (Constants.FORWARD_SLASH.equals(path) ? "" : Constants.FORWARD_SLASH) + name;
    }

    private String byte2String(byte[] value) {
        return null == value ? "" : new String(value, StandardCharsets.UTF_8);
    }

}
