package com.ly.fn.inf.xpro.plugin.api.annotation;

import java.lang.annotation.*;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2017年11月27日 下午4:43:56
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XproAppConfig {
    String value();
    String gatewayName() default "";
}
