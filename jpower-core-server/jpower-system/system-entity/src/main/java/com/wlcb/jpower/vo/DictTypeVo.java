package com.wlcb.jpower.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wlcb.jpower.dbs.entity.dict.TbCoreDictType;
import com.wlcb.jpower.module.common.node.Node;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DictVo
 * @Description TODO
 * @Author 郭丁志
 * @Date 2020/8/21 0021 22:05
 * @Version 1.0
 */
@Data
public class DictTypeVo extends TbCoreDictType implements Node {

    private static final long serialVersionUID = -6865706768426898476L;

    @ApiModelProperty("语言类型")
    private String delEnabledStr;

    @ApiModelProperty("语言类型")
    private String isTreeStr;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Node> children;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Boolean hasChildren;

    @Override
    public List<Node> getChildren() {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        return this.children;
    }

    /**
     * 是否有子孙节点
     */
    @Override
    public Boolean getHasChildren() {
        if (children.size() > 0) {
            return true;
        } else {
            return this.hasChildren;
        }
    }

}