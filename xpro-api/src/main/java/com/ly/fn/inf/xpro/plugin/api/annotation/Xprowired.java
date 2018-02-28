package com.ly.fn.inf.xpro.plugin.api.annotation;

import java.lang.annotation.*;

/**
 * @author Mitsui
 * @since 2017年11月17日
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Xprowired {
}