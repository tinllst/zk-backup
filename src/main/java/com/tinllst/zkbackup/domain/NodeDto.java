package com.tinllst.zkbackup.domain;

import lombok.Getter;

/**
 * @author tinllst
 */
@Getter
public class NodeDto {

    private String nodePath;
    private String value;

    public NodeDto(String nodePath, String value) {
        super();
        this.nodePath = nodePath;
        this.value = value;
    }

}
