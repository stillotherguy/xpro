package com.ly.fn.inf.xpro.plugin.core.util;

/**
 * 反射工具类。
 */
public class ReflectUtil {
    /**
     * 是否祖先类。
     */
    public static boolean isAncestor(Class<?> ancestor, Class<?> child) {
        if (ancestor.equals(child)) {
            return true;
        }

        for (Class<?> infClass : child.getInterfaces()) {
            if (isAncestor(ancestor, infClass)) {
                return true;
            }
        }

        Class<?> superClass = child.getSuperclass();
        if (superClass == null) {
            return false;
        }

        return isAncestor(ancestor, superClass);
    }
}
