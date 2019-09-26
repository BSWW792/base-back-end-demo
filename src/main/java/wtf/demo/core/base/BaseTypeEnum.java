package wtf.demo.core.base;

/**
 * 基础枚举类
 * @author gongjf
 * @since 2019年6月13日 14:56:46
 */
public interface BaseTypeEnum {

    String getDisplay();

    Integer getValue();

    static <T extends BaseTypeEnum> T toEnum(Class<T> enumType, Integer value) {
        T[] values = enumType.getEnumConstants();
        if(value == null) return values[0];

        for(int i = 0; i < values.length; ++i) {
            T t = values[i];
            if (t.getValue().equals(value)) {
                return t;
            }
        }

        return values[0];
    }

    static <T extends BaseTypeEnum> T toEnumByName(Class<T> enumType, String name) {
        T[] values = enumType.getEnumConstants();
        if(name == null) return values[0];

        for(int i = 0; i < values.length; ++i) {
            T t = values[i];
            if (t.toString().equals(name)) {
                return t;
            }
        }

        return values[0];
    }

    static <T extends BaseTypeEnum> T toEnumByDisplay(Class<T> enumType, String display) {
        T[] values = enumType.getEnumConstants();
        if(display == null) return values[0];

        for(int i = 0; i < values.length; ++i) {
            T t = values[i];
            if (t.getDisplay().equals(display)) {
                return t;
            }
        }

        return values[0];
    }

}
