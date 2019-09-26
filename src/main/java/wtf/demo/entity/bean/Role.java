package wtf.demo.entity.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wtf.demo.core.annotation.*;
import wtf.demo.core.base.Base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**   
 * 角色
 * @author gongjf
 * @since 2019年3月1日 上午9:37:49
 */
@ApiModel("角色")
@DBTable("sys_role")
@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends Base {
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

    @ApiModelProperty("名称")
    @DBTableColumn("name")
    private String name;

    @ApiModelProperty("编码")
    @DBTableColumn("code")
    private String code;

    @ApiModelProperty("菜单键")
    @DBTableColumn("menu_key")
    private String menuKey;

    @ApiModelProperty("菜单集合")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Menu> menus;

}
