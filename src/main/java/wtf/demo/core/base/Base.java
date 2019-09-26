package wtf.demo.core.base;

import lombok.Data;

import java.io.Serializable;

/**
 * 基础类
 * @author gongjf
 * @since 2019年6月5日 14:16:11
 */
@Data
public abstract class Base implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    @Override
    public Base clone() throws CloneNotSupportedException {
        return (Base) super.clone();
    }

}
