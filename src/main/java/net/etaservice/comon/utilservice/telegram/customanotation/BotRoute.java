package net.etaservice.comon.utilservice.telegram.customanotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BotRoute { // Use Define Class Bot Route and allow use BotCallBack
    String value() default "";
}
