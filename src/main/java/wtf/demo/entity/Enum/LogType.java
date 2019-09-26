package wtf.demo.entity.Enum;

import wtf.demo.core.base.BaseTypeEnum;

/**
 * 日志类型
 * @author gongjf
 * @since 2019年4月4日 下午2:45:30
 */
public enum LogType implements BaseTypeEnum {
    Operation(1, "操作"),
    Exception(2, "异常"),
    ThirdParty(3, "第三方系统");


    private Integer value;

    private String display;


    LogType(int value, String display) {
        this.value = value;
        this.display = display;
    }


    @Override
    public String getDisplay() {
        return display;
    }

    @Override
    public Integer getValue() {
        return value;
    }

}
