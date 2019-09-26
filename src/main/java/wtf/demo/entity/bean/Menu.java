package wtf.demo.entity.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wtf.demo.core.annotation.*;
import wtf.demo.core.base.Base;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**   
 * 菜单
 * @author gongjf
 * @since 2019年3月1日 上午9:37:49
 * @apiNote 菜单深度只允许到二级
 */
@ApiModel("菜单")
@DBTable("sys_menu")
@Data
@EqualsAndHashCode(callSuper = true)
public class Menu extends Base {
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

    @ApiModelProperty("是否为菜单组")
    @DBTableColumn("is_module")
    private Boolean isModule;

    @ApiModelProperty("父级ID")
    @DBTableColumn("parent_id")
    private String parentId;

    @ApiModelProperty("描述")
    @DBTableColumn("description")
    private String description;

    @ApiModelProperty("子菜单集合")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Menu> children;


    public Menu() {
        super();
    }

    public Menu(String code) {
        super();
        this.code = code;
    }

}
