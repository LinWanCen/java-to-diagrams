package io.github.linwancen.diagrams.java.api.bean;

import io.github.linwancen.util.java.CommentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JavaInfo extends ModifiersInfo {
    /** 包名 */
    public List<String> packNames = new ArrayList<>();
    /** 包注释 */
    public List<String> packComments = new ArrayList<>();

    /** 全称 */
    public String sign;
    /** 简称 */
    public String name;
    /** 首字母小写简称 */
    public String lowFirstName;

    /** 首句注释 */
    public String commentFirst;
    /** 全部注释 */
    public String comment;

    /** 是否选择的文件 */
    public boolean isSelect;


    /**
     * 生成首字母小写简称
     */
    public void genLowFirstName() {
        lowFirstName = name.substring(0, 1).toLowerCase() + name.substring(1);
    }


    private static final Logger LOG = LoggerFactory.getLogger(JavaInfo.class);

    /**
     * 截取注释第一句
     */
    public void genCommentFirst() {
        if (comment == null) {
            commentFirst = "";
            return;
        }
        String firstComment = CommentUtils.firstComment(comment);
        if (firstComment != null) {
            commentFirst = firstComment;
        } else {
            logCommentFirst();
            commentFirst = comment;
        }
    }

    protected void logCommentFirst() {
        LOG.warn("commentGenFirst fail:\n{}({}.java:1) \n{}", sign, name, comment);
    }
}
