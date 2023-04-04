package arx.dukalis.unit.api;

import arx.dukalis.unit.internal.DefaultTestException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@UnitAnnotation
public @interface Test {
    Class<?> shouldRaise() default DefaultTestException.class;
}