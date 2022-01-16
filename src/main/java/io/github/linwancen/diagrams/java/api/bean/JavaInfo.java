package io.github.linwancen.diagrams.java.api.bean;

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
    public String[] commentLines;
    /** 全部注释 */
    public String comment;

    /** 是否选择的文件 */
    public boolean isSelect;

    /** 行数 */
    public int lineCount;

    /**
     * 生成首字母小写简称
     */
    public void genLowFirstName() {
        lowFirstName = name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public String getComment(int index) {
        if (commentLines != null && commentLines.length > index) {
            return commentLines[index];
        }
        return null;
    }

    public String getCommentNotNull(int index) {
        if (commentLines != null && commentLines.length > index) {
            return commentLines[index];
        }
        return "";
    }

    public String getCommentLine(int index) {
        if (commentLines != null && commentLines.length > index) {
            return commentLines[index] + "\n";
        }
        return "";
    }
}
