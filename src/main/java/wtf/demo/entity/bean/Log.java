package wtf.demo.entity.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wtf.demo.core.annotation.*;
import wtf.demo.core.base.Base;
import wtf.demo.core.base.BaseTypeEnum;
import wtf.demo.entity.Enum.LogType;

import java.util.Date;
import java.util.Map;

/**
 * 日志
 * @author gongjf
 * @since 2019年6月13日 15:23:25
 */
@ApiModel("日志")
@DBTable("sys_log")
@Data
@EqualsAndHashCode(callSuper = true)
public class Log extends Base {
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

    @ApiModelProperty("创建者名称")
    @DBTableColumn("creator")
    private String creator;

    @ApiModelProperty("创建者ID")
    @DBTableColumn("creator_id")
    private String creatorId;

    @ApiModelProperty("方法名称")
    @DBTableColumn("method")
    private String method;

    @ApiModelProperty("方法参数")
    @DBTableColumn("args")
    private Map<String, Object> args;

    @ApiModelProperty("耗时")
    @DBTableColumn("spend_time")
    private Long spendTime;

    @ApiModelProperty("日志类型")
    @DBTableColumn("log_type")
    private LogType logType;

    @ApiModelProperty("日志类型ID串")
    private String logTypeIds;

    @ApiModelProperty("IP地址")
    @DBTableColumn("ip")
    private String ip;

    @ApiModelProperty("主机名")
    @DBTableColumn("host")
    private String host;

    @ApiModelProperty("URL")
    @DBTableColumn("url")
    private String url;


    public Log() {
        super();
    }


    public Integer getLogType() {
        if(logType != null) return logType.getValue();
        return null;
    }

    public void setLogType(Integer logType) {
        this.logType = BaseTypeEnum.toEnum(LogType.class, logType);
    }

    public String getLogTypeDisplay() {
        if(logType != null) return logType.getDisplay();
        return null;
    }

    public LogType getLogTypeEnum() {
        return logType;
    }

    public void setLogTypeEnum(LogType logType) {
        this.logType = logType;
    }

}
