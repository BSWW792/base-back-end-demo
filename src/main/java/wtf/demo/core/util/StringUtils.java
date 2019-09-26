package wtf.demo.core.util;

import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author gongjf
 * @since 2019年6月13日 16:26:08
 */
public abstract class StringUtils {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    private StringUtils() {}

    /**
     * 获得首字母小写类名（不含包名）
     * @param type Class
     * @return String
     */
    public static String getLowerName(Class type) {
        String str = type.getSimpleName();
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 将数组组合成字符串
     * @param objects   Object[] 数组
     * @param separator String 分隔符
     * @return String
     */
    public static String getString(Object[] objects, String separator) {
        if (objects == null) {
            return null;
        }
        if (objects.length == 0) {
            return "";
        } else {
            StringBuilder rs = new StringBuilder();
            for (Object obj : objects) {
                rs.append(obj.toString());
                rs.append(separator);
            }
            return rs.substring(0, rs.length() - separator.length());
        }
    }

    /**
     * 将以逗号为分隔符的字符串转换为整数数组. <br>
     * 字符串为null时返回null，为空时返回零长度数组.
     * @param str String 要转换的字符串
     * @return int[]
     */
    public static int[] getInts(String str) {
        if (str == null) {
            return null;
        }
        String[] s = str.split(",");
        int[] rs = new int[s.length];
        for (int i = 0; i < s.length; i++) {
            rs[i] = Integer.parseInt(s[i]);
        }
        return rs;
    }

    /**
     * 将逗号分隔的字符串转化为字符串数组，并除去前导和后缀空格.
     * @param str String
     * @return String[] 字符串为null时，返回null
     */
    public static String[] getStrings(String str) {
        if (str == null) {
            return null;
        }
        String[] s = str.split(",");
        for (int i = 0; i < s.length; i++) {
            s[i] = s[i].trim();
        }
        return s;
    }

    /**
     * 将以逗号为分隔符的字符串转换为整数数组. <br>
     * 字符串为null时返回null，为空时返回零长度数组.
     * @param str String 要转换的字符串
     * @return int[]
     */
    public static Integer[] getIntegers(String str) {
        if (str != null) {
            if (str.length() == 0) {
                return new Integer[0];
            }
            String[] s = str.split(",");
            Integer[] rs = new Integer[s.length];
            for (int i = 0; i < s.length; i++) {
                rs[i] = new Integer(s[i]);
            }
            return rs;
        }
        return null;
    }

    /**
     * 判断字符串为null或空
     * @param str String
     * @return boolean
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符串不为null非不为空（即长度大于0）
     * @param str String
     * @return boolean
     */
    public static boolean isNotEmpty(String str) {
        return str != null && str.length() > 0;
    }

    /**
     * 得到前面length个字符，不足加上后缀postfix
     * @param str     String
     * @param postfix String
     * @param length  int
     * @return String
     */
    public static String getLeft(String str, String postfix, int length) {
        if (str != null && str.length() > length) {
            str = str.substring(0, length);
            if (postfix != null) {
                str = str + postfix;
            }
            return str;
        }
        return str;
    }

    /**
     * 得到固定长度的字符
     * @param str    String 原字符
     * @param length int 长度
     * @param prefix char 长度不足时的补足字符
     * @return String
     */
    public static String getFixed(String str, int length, char prefix) {
        int k = str.length();
        if (k > length) {
            throw new IllegalArgumentException(
                    "String length must less than or equals " + length);
        }
        StringBuilder rs = new StringBuilder();
        for (int i = length - k; i > 0; i--) {
            rs.append(prefix);
        }
        return rs.toString() + str;
    }

    public static String convertUnitType(Integer typeId) {
        String strType = typeId.toString();
        StringBuffer temp = new StringBuffer();
        for (int i = 0; i < strType.length(); i++) {
            temp.append(intToString(strType.substring(i, i + 1)));
        }
        return temp.toString();
    }

    public static String intToString(String typeId) {
        String type = null;
        switch (Integer.parseInt(typeId)) {
            case 0:
                type = "a";
                break;
            case 1:
                type = "b";
                break;
            case 2:
                type = "c";
                break;
            case 3:
                type = "d";
                break;
            case 4:
                type = "e";
                break;
            case 5:
                type = "f";
                break;
            case 6:
                type = "g";
                break;
            case 7:
                type = "h";
                break;
            case 8:
                type = "i";
                break;
            case 9:
                type = "j";
                break;
            default:
                System.out.print("this char needn't to be changed");
        }

        return type;
    }

    /**
     * 半角转全角
     * @param input String.
     * @return 全角字符串.
     */
    public static String ToSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000'; // 采用十六进制,相当于十进制的12288

            } else if (c[i] < '\177') { // 采用八进制,相当于十进制的127
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     * @param input String.
     * @return 半角字符串
     */
    public static String ToDBC(String input) {

        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);

        return returnString;
    }

    public static String Html2Text(String inputString) {
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        Pattern p_script;
        java.util.regex.Matcher m_script;
        Pattern p_style;
        java.util.regex.Matcher m_style;
        Pattern p_html;
        java.util.regex.Matcher m_html;

        try {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            // }
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            // }
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签

            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签

            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签

            textStr = htmlStr;

        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }

        return textStr;// 返回文本字符串
    }

    /**
     * 一次性判断多个或单个对象为空。
     * @param objects
     * @return 只要有一个元素为Blank，则返回true
     */
    public static boolean isBlank(Object... objects) {
        Boolean result = false;
        for (Object object : objects) {
            if (null == object || "".equals(object.toString().trim())
                    || "null".equals(object.toString().trim())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 一次性判断多个或单个对象不为空。
     * @param objects
     * @return 只要有一个元素不为Blank，则返回true
     */
    public static boolean isNotBlank(Object... objects) {
        return !isBlank(objects);
    }

    public static boolean isBlank(String... objects) {
        Object[] object = objects;
        return isBlank(object);
    }

    public static boolean isNotBlank(String... objects) {
        Object[] object = objects;
        return !isBlank(object);
    }

    public static boolean isBlank(String str) {
        Object object = str;
        return isBlank(object);
    }

    public static boolean isNotBlank(String str) {
        Object object = str;
        return !isBlank(object);
    }

    /**
     * 将以英文逗号（,）分割的字符串转换为list集合
     * @param arge
     */
    public static List<String> commaStringToList(String arge) {
        List<String> list = new ArrayList<String>();
        if (isNotBlank(arge)) {
            String object[] = arge.split(",");
            for (int i = 0; i < object.length; i++) {
                list.add(object[i]);
            }
        }
        return list;
    }

    /**
     * 将字符串转换为list集合
     * @param arge
     */
    public static List<String> stringToList(String arge) {
        List<String> list = new ArrayList<String>();
        if (isNotBlank(arge)) {
            if (arge.contains("[")) {
                list = JacksonUtil.jsonToList(arge, String.class);
            } else {
                if (arge.contains("\"")) {
                    arge = arge.replace("\"", "");
                }
                list = commaStringToList(arge);
            }
        }
        return list;
    }

    /**
     * 将字符串转换为list集合
     * @param arge
     */
    public static List<String> stringToList(String arge, String splitRex) {
        if (DataUtil.isNotEmpty(splitRex)) {
            String[] arr = arge.split(splitRex);
            return java.util.Arrays.asList(arr);
        } else return stringToList(arge);
    }

    /**
     * 将字符串集合转换为指定分隔符的字符串
     * @param strList
     * @param splitRex
     */
    public static String listToString(List<String> strList, String splitRex) {
        StringBuffer sb = new StringBuffer();
        for (String s : strList) {
            sb.append(s);
            sb.append(splitRex);
        }
        String result = null;
        if (sb.length() > 0) {
            result = sb.substring(0, sb.length() - 1);
        }
        return result;
    }

    /**
     * 将字符串集合转换为逗号分隔的字符串
     * @param strList
     */
    public static String listToString(List<String> strList) {
        return listToString(strList, ",");
    }

}
