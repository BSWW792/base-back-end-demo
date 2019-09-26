package wtf.demo.core.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Value;

import java.beans.FeatureDescriptor;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.stream.Stream;

/**
 * 数据工具
 * @author gongjf
 * @since 2018-11-08
 */
public final class DataUtil {

    private static boolean testEnvironment = false;

    public boolean isTestEnvironment() {
        return testEnvironment;
    }
    @Value("${system.test-environment}")
    public void setTestEnvironment(boolean testEnvironment) {
        DataUtil.testEnvironment = testEnvironment;
    }

    private DataUtil() {
    }

    /**
     * 十进制字节数组转十六进制字符串
     *
     * @param b
     * @return
     */
    public static final String byte2hex(byte[] b) { // 一个字节数，转成16进制字符串
        StringBuilder hs = new StringBuilder(b.length * 2);
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            // 整数转成十六进制表示
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append("0").append(stmp);
            else
                hs.append(stmp);
        }
        return hs.toString(); // 转成大写
    }

    /**
     * 十六进制字符串转十进制字节数组
     *
     * @param hs
     * @return
     */
    public static final byte[] hex2byte(String hs) {
        byte[] b = hs.getBytes();
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("长度不是偶数");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个十进制字节
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    /**
     * 这个方法可以通过与某个类的class文件的相对路径来获取文件或目录的绝对路径。 通常在程序中很难定位某个相对路径，特别是在B/S应用中。
     * 通过这个方法，我们可以根据我们程序自身的类文件的位置来定位某个相对路径。
     * 比如：某个txt文件相对于程序的Test类文件的路径是../../resource/test.txt，
     * 那么使用本方法Path.getFullPathRelateClass("../../resource/test.txt",Test.class)
     * 得到的结果是txt文件的在系统中的绝对路径。
     *
     * @param relatedPath 相对路径
     * @param cls         用来定位的类
     * @return 相对路径所对应的绝对路径
     * @throws IOException 因为本方法将查询文件系统，所以可能抛出IO异常
     */
    public static final String getFullPathRelateClass(String relatedPath, Class<?> cls) {
        String path = null;
        if (relatedPath == null) {
            throw new NullPointerException();
        }
        String clsPath = getPathFromClass(cls);
        File clsFile = new File(clsPath);
        String tempPath = clsFile.getParent() + File.separator + relatedPath;
        File file = new File(tempPath);
        try {
            path = file.getCanonicalPath();
        } catch (IOException e) {
            if(testEnvironment) e.printStackTrace();
        }
        return path;
    }

    /**
     * 获取class文件所在绝对路径
     *
     * @param cls
     * @return
     * @throws IOException
     */
    public static final String getPathFromClass(Class<?> cls) {
        String path = null;
        if (cls == null) {
            throw new NullPointerException();
        }
        URL url = getClassLocationURL(cls);
        if (url != null) {
            path = url.getPath();
            if ("jar".equalsIgnoreCase(url.getProtocol())) {
                try {
                    path = new URL(path).getPath();
                } catch (MalformedURLException e) {
                }
                int location = path.indexOf("!/");
                if (location != -1) {
                    path = path.substring(0, location);
                }
            }
            File file = new File(path);
            try {
                path = file.getCanonicalPath();
            } catch (IOException e) {
                if(testEnvironment) e.printStackTrace();
            }
        }
        return path;
    }

    /**
     * 判断对象是否Empty(null或元素为0)<br>
     * 实用于对如下对象做判断:String Collection及其子类 Map及其子类
     *
     * @param pObj 待检查对象
     * @return boolean 返回的布尔值
     */
    public static final boolean isEmpty(Object pObj) {
        if (pObj == null)
            return true;
        if (pObj instanceof String) {
            if (((String) pObj).trim().length() == 0) {
                return true;
            }
            if ("null".equals(((String) pObj).trim())) {
                return true;
            }
        } else if(pObj instanceof StringBuffer) {
            if(((StringBuffer) pObj).toString().trim().length() == 0) {
                return true;
            }
            if ("null".equals(((StringBuffer) pObj).toString().trim())) {
                return true;
            }
        } else if (pObj instanceof Map) {
            if (((Map<String, Object>) pObj).size() == 0) {
                return true;
            }
        } else if (pObj instanceof Map<?, ?>) {
            if (((Map<?, ?>) pObj).size() == 0) {
                return true;
            }
        } else if (pObj instanceof Collection<?>) {
            if (((Collection<?>) pObj).size() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断对象是否为NotEmpty(!null或元素>0)<br>
     * 实用于对如下对象做判断:String Collection及其子类 Map及其子类
     *
     * @param pObj 待检查对象
     * @return boolean 返回的布尔值
     */
    public static final boolean isNotEmpty(Object pObj) {
        if (pObj == null)
            return false;
        if (pObj instanceof String) {
            if (((String) pObj).trim().length() == 0) {
                return false;
            }
            if ("null".equals(((String) pObj).trim())) {
                return false;
            }
        } else if(pObj instanceof StringBuffer) {
            if(((StringBuffer) pObj).toString().trim().length() == 0) {
                return false;
            }
            if ("null".equals(((StringBuffer) pObj).toString().trim())) {
                return false;
            }
        } else if (pObj instanceof Map) {
            if (((Map<String, Object>) pObj).size() == 0) {
                return false;
            }
        } else if (pObj instanceof Map<?, ?>) {
            if (((Map<?, ?>) pObj).size() == 0) {
                return false;
            }
        } else if (pObj instanceof Collection<?>) {
            if (((Collection<?>) pObj).size() == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * JS输出含有\n的特殊处理
     *
     * @param pStr
     * @return
     */
    public static final String replace4JsOutput(String pStr) {
        pStr = pStr.replace("\r\n", "<br/>&nbsp;&nbsp;");
        pStr = pStr.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
        pStr = pStr.replace(" ", "&nbsp;");
        return pStr;
    }

    /**
     * 分别去空格
     *
     * @param paramArray
     * @return
     */
    public static final String[] trim(String[] paramArray) {
        if (DataUtil.isEmpty(paramArray)) {
            return paramArray;
        }
        String[] resultArray = new String[paramArray.length];
        for (int i = 0; i < paramArray.length; i++) {
            String param = paramArray[i];
            resultArray[i] = param.trim();
        }
        return resultArray;
    }

    /**
     * 获取类的class文件位置的URL
     *
     * @param cls
     * @return
     */
    private static URL getClassLocationURL(final Class<?> cls) {
        if (cls == null)
            throw new IllegalArgumentException("null input: cls");
        URL result = null;
        final String clsAsResource = cls.getName().replace('.', '/').concat(".class");
        final ProtectionDomain pd = cls.getProtectionDomain();
        if (pd != null) {
            final CodeSource cs = pd.getCodeSource();
            if (cs != null)
                result = cs.getLocation();
            if (result != null) {
                if ("file".equals(result.getProtocol())) {
                    try {
                        if (result.toExternalForm().endsWith(".jar") || result.toExternalForm().endsWith(".zip"))
                            result = new URL("jar:".concat(result.toExternalForm()).concat("!/").concat(clsAsResource));
                        else if (new File(result.getFile()).isDirectory())
                            result = new URL(result, clsAsResource);
                    } catch (MalformedURLException ignore) {
                    }
                }
            }
        }
        if (result == null) {
            final ClassLoader clsLoader = cls.getClassLoader();
            result = clsLoader != null ? clsLoader.getResource(clsAsResource)
                    : ClassLoader.getSystemResource(clsAsResource);
        }
        return result;
    }

    /**
     * 初始化设置默认值
     */
    public static final <K> K ifNull(K k, K defaultValue) {
        if (k == null) {
            return defaultValue;
        }
        return k;
    }

    public static List<String> changeArrayToList(String arge[]) {
        List<String> list = new ArrayList<String>();
        if (arge != null && arge.length > 0) {
            for (int i = 0; i < arge.length; i++) {
                list.add(arge[i]);
            }
        }
        return list;

    }


    /**
     * @param arge
     * @return
     * @throws
     * @Title: changeStringToList
     * @Description: 将以英文逗号（,）分割的字符串转换为list集合
     * @author gongjf
     * @date 2018年9月2日19:12:44
     */
    public static List<String> changeStringToList(String arge) {
        List<String> list = new ArrayList<String>();
        if (StringUtils.isNotBlank(arge)) {
            String object[] = arge.split(",");
            for (int i = 0; i < object.length; i++) {
                list.add(object[i]);
            }
        }

        return list;

    }

    /**
     * @param arge
     * @return
     * @throws
     * @Title: changeStringToMap
     * @Description: 将以英文逗号（,）分割的字符串转换为map
     * @author gongjf
     * @date 2018年10月22日14:30:37
     */
    public static Map<String, String> changeStringToMap(String arge) {
        Map<String, String> result = new HashMap<>();
        if (StringUtils.isNotBlank(arge)) {
            String object[] = arge.split(",");
            for (int i = 0; i < object.length; i++) {
                result.put(object[i], object[i]);
            }
        }
        return result;
    }

    /**
     * @param arge
     * @return
     * @throws
     * @Title: changeStringToList
     * @Description: 将以英文逗号（,）分割的字符串转换为list集合
     * @author gongjf
     * @date 2018年9月2日19:12:34
     */
    public static List<Double> changeStringToDoubleList(String arge) {
        List<Double> list = new ArrayList<Double>();
        if (StringUtils.isNotBlank(arge)) {
            String object[] = arge.split(",");
            for (int i = 0; i < object.length; i++) {
                list.add(Double.parseDouble(object[i]));
            }
        }

        return list;

    }

    /**
     * @param arge
     * @return
     * @throws
     * @Title: changeStringToList
     * @Description: 将以英文逗号（,）分割的字符串转换为list集合
     * @author gongjf
     * @date 2018年9月2日19:11:25
     */
    public static List<Integer> changeStringToIntegerList(String arge) {
        List<Integer> list = new ArrayList<Integer>();
        if (StringUtils.isNotBlank(arge)) {
            String object[] = arge.split(",");
            for (int i = 0; i < object.length; i++) {
                list.add(Integer.parseInt(object[i]));
            }
        }

        return list;
    }

    /**
     * @param strMap
     * @return
     * @方法：changeMapToString
     * @描述：将Map对象转换成一逗号分隔的字符串不带引号
     * @author： gongjf
     * @date : 2018年10月24日17:02:43
     * @version： V1.0
     */
    public static String changeMapToString(Map<String, String> strMap) {
        String strs = "";
        for (Map.Entry<String, String> entry : strMap.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue())) {
                if (strs == "") {
                    strs = entry.getValue();
                } else {
                    strs += "," + entry.getValue();
                }
            }
        }
        return strs;
    }

    /**
     * @param strList
     * @return
     * @方法：changeListToString
     * @描述：将list集合转换成一逗号分隔的字符串不带引号
     * @author： gongjf
     * @date : 2018年9月2日19:11:03
     * @version： V1.0
     */
    public static String changeListToString(List<String> strList) {
        String strs = "";
        if (strList != null && strList.size() > 0) {
            for (String str : strList) {
                if (StringUtils.isNotBlank(str)) {
                    if (strs == "") {
                        strs = str;
                    } else {
                        strs += "," + str;
                    }
                }
            }
        }
        return strs;

    }

    /**
     * @param strList
     * @return
     * @方法：changeListToString
     * @描述：将list集合转换成一逗号分隔的字符串带引号
     * @author： gongjf
     * @date : 2018年9月2日19:10:34
     * @version： V1.0
     */
    public static String changeListToString2(List<String> strList) {
        String strs = "";
        if (strList != null && strList.size() > 0) {
            for (String str : strList) {
                if (StringUtils.isNotBlank(str)) {
                    if (strs == "") {
                        strs = "'" + str + "'";
                    } else {
                        strs += ",'" + str + "'";
                    }
                }
            }
        }
        return strs;
    }

    /**
     * @param ids
     * @return
     * @方法：changeListToString
     * @描述：将普通String转换成一逗号分隔的字符串带引号
     * @author： gongjf
     * @date : 2018年11月30日08:56:49
     * @version： V1.0
     */
    public static String changeStringToString(String ids) {
        List<String> strList = changeStringToList(ids);
        String strs = changeListToString2(strList);
        return strs;
    }

    /**
     * @param arge
     * @return
     * @throws
     * @Title: changeStringToNumString
     * @Description: 将以英文逗号（,）分割的字符串转去掉除数字外的其他字符
     * @author gongjf
     * @date 2018年9月2日19:10:25
     */
    public static String changeStringToNumString(String arge) {
        String resultStr = null;
        if (StringUtils.isNotBlank(arge)) {
            String object[] = arge.split(",");
            for (int i = 0; i < object.length; i++) {
                if (StringUtils.isNotBlank(object[i])) {
                    String str = object[i].replaceAll("[^0-9]", "");
                    if (resultStr == null) {
                        resultStr = str;
                    } else {
                        resultStr += "," + str;
                    }
                }
            }
        }
        return resultStr;
    }

    /**
     * 行政区划截取最短编码（从字符尾部截取“00”，例如浙江编码：330000，返回值33，可用于模糊查询，330101返回330101）
     *
     * @param areaCode
     * @return
     */
    public static final String getShortAreaCode(String areaCode) {
        if (areaCode.length() > 2) {
            //字符末尾不是00，则返回
            if (!areaCode.substring(areaCode.length() - 2, areaCode.length()).equals("00")) {
                return areaCode;
            } else {
                return getShortAreaCode(areaCode.substring(0, areaCode.length() - 2));
            }
        } else {
            return areaCode;
        }
    }

    /**
     * 兩個 double数相加
     * @param v1
     * @param v2
     * @return
     */
    public static double addDouble(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * map对象对比
     * @param m1
     * @param m2
     * @return
     */
    public static boolean compareMap(Map<String, String> m1, Map<String, String> m2) {
        boolean result = true;
        for (Map.Entry<String, String> entry1 : m1.entrySet()) {
            String m1value = entry1.getValue() == null ? "" : entry1.getValue();
            String m2value = m2.get(entry1.getKey()) == null ? "" : m2.get(entry1.getKey());
            if (!m1value.equals(m2value)) {//若两个map中相同key对应的value不相等
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * 判断两个map对象是否一致
     * @param map1
     * @param map2
     * @return
     */
    public static boolean equalMap(Map<String, String> map1, Map<String, String> map2) {
        return compareMap(map1, map2) && compareMap(map2, map1);
    }

    /**
     * 获取对象的空值属性名
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }
}