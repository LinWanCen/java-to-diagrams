package io.github.linwancen.util.java;

import java.util.regex.Pattern;

/**
 * 筛选工具类
 */
public class FilterUtils {

    private FilterUtils() {}

    /**
     * 正则包括与排除
     */
    public static boolean filter(String str, Pattern include, Pattern exclude) {
        if (exclude != null && exclude.pattern().length() > 0 && exclude.matcher(str).find()) {
            return false;
        }
        if (include != null && include.pattern().length() > 0) {
            return include.matcher(str).find();
        }
        return true;
    }
}
