package io.github.linwancen.util.java;

import java.util.regex.Pattern;

public class CommentUtils {

    private CommentUtils() {}

    private static final Pattern LINE_PATTERN = Pattern.compile(" *[\r\n]+ *");

    /**
     * 切割注释并 trim
     */
    public static String[] splitToLines(String comment) {
        if (comment == null) {
            return new String[0];
        }
        return LINE_PATTERN.split(comment);
    }

    private static final Pattern BR_PATTERN = Pattern.compile("[\r\n]{0,2}</?[^>]++/?>[\r\n]{0,2}");
    private static final Pattern A_PATTERN = Pattern.compile("\\{@\\w* (?:[^}]+\\.)?([^.}]++)}");

    /**
     * 去除 html 和 大括号 标签
     */
    public static String textFrom(String javadoc) {
        javadoc = BR_PATTERN.matcher(javadoc).replaceAll("\n");
        javadoc = A_PATTERN.matcher(javadoc).replaceAll("$1");
        return javadoc;
    }
}
