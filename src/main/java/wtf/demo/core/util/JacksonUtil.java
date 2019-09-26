package wtf.demo.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * json转换工具类
 * @author gongjf
 * @since 2019年6月18日 15:04:32
 *
 * ObjectMapper是JSON操作的核心，Jackson的所有JSON操作都是在ObjectMapper中实现。
 * ObjectMapper有多个JSON序列化的方法，可以把JSON字符串保存File、OutputStream等不同的介质中。
 * ObjectMapper支持从byte[]、File、InputStream、字符串等数据的JSON反序列化。
 * writeValue(File arg0, Object arg1)把arg1转成json序列，并保存到arg0文件中。
 * writeValue(OutputStream arg0, Object arg1)把arg1转成json序列，并保存到arg0输出流中。
 * writeValueAsBytes(Object arg0)把arg0转成json序列，并把结果输出成字节数组。
 * writeValueAsString(Object arg0)把arg0转成json序列，并把结果输出成字符串。
 */
@Component
@Slf4j
public class JacksonUtil {

    @Value("${system.test-environment}")
    private static boolean testEnvironment = false;

    public boolean isTestEnvironment() {
        return testEnvironment;
    }
    @Value("${system.test-environment}")
    public void setTestEnvironment(boolean testEnvironment) {
        JacksonUtil.testEnvironment = testEnvironment;
    }

    private static String dateFormat;

    private static String timeZone;

    private static ObjectMapper mapper = new ObjectMapper();

    // 会优先于getter/setter方法执行
    static {
        /////// 序列化
        // Include.Include.ALWAYS 默认
        // Include.NON_DEFAULT 属性为默认值不序列化
        // Include.NON_EMPTY 属性为 空（""） 或者为 NULL 都不序列化，则返回的json是没有这个字段的。这样对移动端会更省流量
        // Include.NON_NULL 属性为NULL 不序列化
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        // 时间戳使用数值timestamp表示日期
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 对于空的对象转json的时候不抛出错误
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        /////// 反序列化
        // 解决实体未包含字段反序列化时抛出异常
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        /////// 语法
        // 允许属性名称没有引号
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许单引号
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 允许出现特殊字符和转义符
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    @Value("${spring.jackson.date-format}")
    public void setDateFormat(String format) {
        dateFormat = format;
        // 设置时间格式
        mapper.setDateFormat(new SimpleDateFormat(dateFormat));
    }

    @Value("${spring.jackson.time-zone}")
    public void setTimeZone(String zone) {
        timeZone = zone;
        // 设置时区
        mapper.setTimeZone(TimeZone.getTimeZone(timeZone));
    }

    /**
     * 将一个object转换为json, 可以是一个java对象，也可以是集合
     * @param obj - 传入的数据
     * @return
     */
    public static String objectToJson(Object obj) {
        String json = null;
        try {
            json = mapper.writeValueAsString(obj);
        } catch (Exception e) {
            if(testEnvironment) e.printStackTrace();
            log.error(e.getMessage());
        }
        return json;
    }

    /**
     * 将一个object转换为Map, 可以使一个java对象，也可以使集合
     * @param obj - 传入的数据
     * @return
     */
    public static Map objectToMap(Object obj) {
        Map map = null;
        try {
            String json = mapper.writeValueAsString(obj);
            map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            if(testEnvironment) e.printStackTrace();
            log.error(e.getMessage());
        }
        return map;
    }

    /**
     * 将json数据转换成list
     * @param json - 转换的数据
     * @return
     */
    public static List<Object> jsonToList(String json) {
        List<Object> list = null;
        try {
            list = mapper.readValue(json, new TypeReference<List<Object>>() {});
        } catch (Exception e) {
            if(testEnvironment) e.printStackTrace();
            log.error(e.getMessage());
        }
        return list;
    }

    /**
     * 将json数据转换成list
     * @param json - 转换的数据
     * @return
     */
    public static <T> List<T> jsonToList(String json, Class<T> beanType) {
        List<T> list = null;
        try {
            JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, beanType);
            list = mapper.readValue(json, javaType);
        } catch (Exception e) {
            if(testEnvironment) e.printStackTrace();
            log.error(e.getMessage());
        }
        return list;
    }

    /**
     * 将json数据转换成Map
     * @param json - 转换的数据
     * @return
     */
    public static Map<String, Object> jsonToMap(String json) {
        Map<String, Object> map = null;
        try {
            map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            if(testEnvironment) e.printStackTrace();
            log.error(e.getMessage());
        }
        return map;
    }

    /**
     * 将json数据转换成Map
     * @param json - 转换的数据
     * @return
     */
    public static <T> Map<String, T> jsonToMap(String json, Class<T> beanType) {
        Map<String, T> map = null;
        try {
            JavaType javaType = mapper.getTypeFactory().constructParametricType(Map.class, String.class, beanType);
            map = mapper.readValue(json, javaType);
        } catch (Exception e) {
            if(testEnvironment) e.printStackTrace();
            log.error(e.getMessage());
        }
        return map;
    }

    /**
     * 将json结果集转化为对象
     * @param json     - json数据
     * @param beanType - 转换的实体类型
     * @return
     */
    public static <T> T jsonToClass(String json, Class<T> beanType) {
        T t = null;
        try {
            t = mapper.readValue(json, beanType);
        } catch (Exception e) {
            if(testEnvironment) e.printStackTrace();
            log.error(e.getMessage());
        }
        return t;
    }

    /**
     * 将一个object转换为json, 可以使一个java对象，也可以使集合
     * @param map - 传入的集合
     * @return
     */
    public static <T> T mapToClass(Map map, Class<T> beanType) {
        T t = null;
        try {
            String json = mapper.writeValueAsString(map);
            t = mapper.readValue(json, beanType);
        } catch (Exception e) {
            if(testEnvironment) e.printStackTrace();
            log.error(e.getMessage());
        }
        return t;
    }

    /**
     * 获取json对象数据的属性对应值
     * @param json - 请求的数据
     * @param propName  - 请求的属性
     * @return 返回String类型数据
     */
    public static String getProp(String json, String propName) {
        String result = null;
        try {
            Map map = jsonToMap(json);
            Object obj = map.get(propName);
            result = JacksonUtil.objectToJson(obj);
        } catch (Exception e) {
            if(testEnvironment) e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 设置json对象数据的属性对应值
     * @param json - 请求的数据
     * @param propName  - 请求的属性
     * @return 返回String类型数据
     */
    public static String setProp(String json, String propName, Object propValue) {
        String result = null;
        try {
            Map map = jsonToMap(json);
            map.put(propName, propValue);
            result = objectToJson(map);
        } catch (Exception e) {
            if(testEnvironment) e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

}
