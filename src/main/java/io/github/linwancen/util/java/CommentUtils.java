package io.github.linwancen.util.java;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentUtils {
    private static final Pattern COMMENT_FIRST_PATTERN = Pattern.compile(
            "^[^\r\n]*");

    /**
     * 首行注释
     */
    public static String firstComment(String comment) {
        Matcher m = COMMENT_FIRST_PATTERN.matcher(comment);
        if (m.find()) {
            return m.group();
        }
        return null;
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
