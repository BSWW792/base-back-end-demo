package wtf.demo.core.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DBTableColumn {
    String value() default "";
    String type() default "";
}
