package io.github.linwancen.diagrams.java.parser;

import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

import java.util.List;
import java.util.Optional;

/**
 * 文档注释工具类
 */
class JavadocUtils {

    private JavadocUtils() {}

    /**
     * 获取文档注释中的标签值
     */
    static String tagComment(Javadoc javadoc, JavadocBlockTag.Type param, String name) {
        List<JavadocBlockTag> blockTags = javadoc.getBlockTags();
        for (JavadocBlockTag tag : blockTags) {
            if (tag.getType() != param) {
                continue;
            }
            String comment = tag.getContent().toText();
            // 没有名字（如返回值）
            if (name == null) {
                return comment;
            }
            // 有名字（如参数、异常）
            Optional<String> commentNameOptional = tag.getName();
            if (!commentNameOptional.isPresent()) {
                continue;
            }
            String commentName = commentNameOptional.get();
            if (commentName.equals(name)) {
                return comment;
            }
        }
        return "";
    }
}
