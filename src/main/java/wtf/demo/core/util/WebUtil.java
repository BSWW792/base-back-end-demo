package wtf.demo.core.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wtf.demo.entity.Enum.ReturnFieldName;
import wtf.demo.entity.Enum.ReturnStatusType;

import java.util.HashMap;
import java.util.Map;

/**
 * Web工具类
 * @author gongjf
 * @since 2019年2月28日 上午9:52:16
 */
@Component
public class WebUtil {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    /**
     * 输出规范信息
     * @param msg
     * @return
     */
    public static Map<String, Object> resultMsg(String msg) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(ReturnFieldName.Status.getValue(), ReturnStatusType.Sucess.getValue());
        result.put(ReturnFieldName.Message.getValue(), DataUtil.isEmpty(msg) ? ReturnStatusType.Sucess.getDisplay() : msg);
        return result;
    }

    /**
     * 输出规范信息
     * @param status
     * @param msg
     * @return
     */
    public static Map<String, Object> resultMsg(ReturnStatusType status, String msg) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(ReturnFieldName.Status.getValue(), status.getValue());
        result.put(ReturnFieldName.Message.getValue(), DataUtil.isEmpty(msg) ? status.getDisplay() : msg);
        return result;
    }

    /**
     * 输出数据
     * @param data
     * @return
     */
    public static Map<String, Object> resultData(Object data) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(ReturnFieldName.Status.getValue(), ReturnStatusType.Sucess.getValue());
        result.put(ReturnFieldName.Message.getValue(), ReturnStatusType.Sucess.getDisplay());
        result.put(ReturnFieldName.Data.getValue(), data);
        return result;
    }

    /**
     * 输出数据和信息
     * @param data
     * @return
     */
    public static Map<String, Object> resultDataMsg(Object data, String msg) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(ReturnFieldName.Status.getValue(), ReturnStatusType.Sucess.getValue());
        result.put(ReturnFieldName.Message.getValue(), DataUtil.isEmpty(msg) ? ReturnStatusType.Sucess.getDisplay() : msg);
        result.put(ReturnFieldName.Data.getValue(), data);
        return result;
    }

}

