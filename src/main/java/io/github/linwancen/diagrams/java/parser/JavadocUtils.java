package io.github.linwancen.diagrams.java.parser;

import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 文档注释工具类
 */
class JavadocUtils {

    private JavadocUtils() {}

    /**
     * 获取文档注释中的单个标签值
     */
    static String tagComment(Javadoc javadoc, JavadocBlockTag.Type param, String name) {
        List<String> tagComments = tagComments(javadoc, param, name);
        if (tagComments.isEmpty()) {
            return "";
        }
        return tagComments.get(0);
    }

    /**
     * 获取文档注释中的标签值
     */
    static List<String> tagComments(Javadoc javadoc, JavadocBlockTag.Type param, String name) {
        List<JavadocBlockTag> blockTags = javadoc.getBlockTags();
        List<String> tagComment = new ArrayList<>();
        for (JavadocBlockTag tag : blockTags) {
            if (tag.getType() != param) {
                continue;
            }
            String comment = tag.getContent().toText();
            // 没有名字（如返回值）
            if (name == null) {
                tagComment.add(comment);
                continue;
            }
            // 有名字（如参数、异常）
            Optional<String> commentNameOptional = tag.getName();
            if (!commentNameOptional.isPresent()) {
                continue;
            }
            String commentName = commentNameOptional.get();
            if (commentName.equals(name)) {
                tagComment.add(comment);
            }
        }
        return tagComment;
    }
}
