package cn.edu.scau.biubiusuisui.annotation;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface FXBind {
    String  [] value();
}
