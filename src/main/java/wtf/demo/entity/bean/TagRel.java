package wtf.demo.entity.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wtf.demo.core.annotation.*;
import wtf.demo.core.base.Base;

import java.util.Date;

/**
 * 标签关联
 * @author gongjf
 * @since 2019年5月1日 上午9:37:49
 */
@ApiModel("标签关联")
@DBTable("sys_tag_rel")
@Data
@EqualsAndHashCode(callSuper = true)
public class TagRel extends Base {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    @DBId
    @DBTableColumn("id")
    private String id;

    @ApiModelProperty("标签ID")
    @DBTableColumn("tag_id")
    private String tagId;

    @ApiModelProperty("关联表")
    @DBTableColumn("rel_id")
    private String relId;

    @ApiModelProperty("关联表")
    @DBTableColumn("rel_table")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String relTable;

    @ApiModelProperty("关联类")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String relBean;

    public TagRel() {}

    public TagRel(String tagId) {
        this.tagId = tagId;
    }

    public TagRel(String relId, String relTable) {
        this.relId = relId;
        this.relTable = relTable;
    }

    public TagRel(String tagId, String relId, String relTable) {
        this.tagId = tagId;
        this.relId = relId;
        this.relTable = relTable;
    }

}
