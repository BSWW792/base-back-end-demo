package wtf.demo.entity.Enum;

import wtf.demo.core.base.BaseTypeEnum;

/**
 * 返回状态类型
 * @author gongjf
 * @since 2016年1月5日上午11:14:40
 */
public enum ReturnStatusType implements BaseTypeEnum {
    UnLogin(-1, "未登陆！"),
    NoAccess(-2, "无使用权限！"),
    HasUnValidParameter(-3, "存在无效参数！"),
    Error(-4, "内部错误！"),
    HasUnDeleteRecord(-5, "存在未删除的记录！"),
    Exceed(-6, "您无对于该范围数据的操作权限！"),
    NoData(-7, "请求的数据不存在！"),
    TIMEOUT(-8, "请求超时"),
    Sucess(0, "操作成功"),
    ApiDataError(-1001, "API参数无效"),
    ClientIdError(-1002, "clientId无效"),
    TimeError(-1003, "time时间戳无效"),
    TokenError(-1004, "token无效");


    private Integer value;

    private String display;


    ReturnStatusType(int value, String display) {
        this.value = value;
        this.display = display;
    }


    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String getDisplay() {
        return this.display;
    }

}
