package wtf.demo.entity.Enum;

/**
 * 返回信息类型
 * @author gongjf
 * @since 2016年1月5日上午11:14:40
 */
public enum ReturnFieldName {
    // 删除操作返回的字段名称
    Delete("result"),
    // 添加操作返回的字段名称
    Add("result"),
    // 更新操作返回的字段名称
    Update("result"),
    // 获取记录个数返回的字段名称
    Count("result"),
    // 获取记录个数返回的字段名称
    Code("result"),
    // 获取记录个数返回的字段名称
    Data("data"),
    // 状态编号
    Status("status"),
    // 错误编号
    Message("message");


    private String value;


    ReturnFieldName(String value) {
        this.value = value;
    }


    public String getValue() {
        return this.value;
    }

}
