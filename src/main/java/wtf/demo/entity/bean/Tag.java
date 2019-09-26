package wtf.demo.entity.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wtf.demo.core.annotation.*;
import wtf.demo.core.base.Base;

import java.util.Date;
import java.util.List;

/**
 * 标签
 * @author gongjf
 * @since 2019年5月1日 上午9:37:49
 */
@ApiModel("标签")
@DBTable("sys_tag")
@Data
@EqualsAndHashCode(callSuper = true)
public class Tag extends Base {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    @DBId
    @DBTableColumn("id")
    private String id;

    @ApiModelProperty("删除标记")
    @DBDelFlag
    @DBTableColumn("del_flag")
    private Boolean delFlag;

    @ApiModelProperty("创建时间")
    @DBCreateTime
    @DBTableColumn("create_time")
    private Date createTime;

    @ApiModelProperty("创建时间截止，用于时间区间查询")
    private Date createTimeEnd;

    @ApiModelProperty("搜索关键字")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String keyword;

    @ApiModelProperty("展示文本")
    @DBTableColumn("label")
    private String label;

    @ApiModelProperty("编码（记录层级和键信息）")
    @DBTableColumn("code")
    private String code;

    @ApiModelProperty("键（自增）")
    @DBTableColumn("key")
    private Integer key;

    @ApiModelProperty("值")
    @DBTableColumn("value")
    private String value;

    @ApiModelProperty("父级ID")
    @DBTableColumn("parent_id")
    private String parentId;

    @ApiModelProperty("层级")
    @DBTableColumn("level")
    private Integer level;

    @ApiModelProperty("标记集合")
    private List<String> marks;

    @ApiModelProperty("子标签集合")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Tag> children;


    public Tag() {
        super();
    }

    public Tag(String code) {
        super();
        this.code = code;
    }

}
