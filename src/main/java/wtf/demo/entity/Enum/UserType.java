package wtf.demo.entity.Enum;

import wtf.demo.core.base.BaseTypeEnum;

/**
 * 用户类型
 * @author gongjf
 * @since 2019年4月4日 下午2:45:30
 */
public enum UserType implements BaseTypeEnum {
    Supremor(1, "最高权限者"),
    Administrator(2, "系统管理员"),
    Manager(3, "业务管理者"),
    Customer(4, "游客");


    private Integer value;

    private String display;


    UserType(int value, String display) {
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
