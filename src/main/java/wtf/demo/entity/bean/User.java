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
 * 用户
 * @author gongjf
 * @since 2019年6月13日 16:16:21
 */
@ApiModel("用户")
@DBTable("sys_user")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends Base {
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

    @ApiModelProperty("密码")
    @DBTableColumn("password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @ApiModelProperty("手机号")
    @DBTableColumn("telephone")
    private String telephone;

    @ApiModelProperty("角色编码")
    @DBTableColumn("role_code")
    private String roleCode;

    @ApiModelProperty("角色集合")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Role> roles;


    public User() {
        super();
    }

    public User(String name) {
        super();
        this.name = name;
    }

}
