package io.github.linwancen.diagrams.java.export.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;

/**
 * EasyExcel 类/方法行
 * <br>类行可以拼接 @Autowired
 * <br>方法行可以拼接整个方法体
 */
@SuppressWarnings("unused")
@HeadStyle(wrapped = BooleanEnum.FALSE, horizontalAlignment = HorizontalAlignmentEnum.LEFT)
@HeadFontStyle(fontHeightInPoints = 11, bold = BooleanEnum.FALSE, fontName="宋体")
public class ExcelLine {
    @ExcelProperty("author")
    private String author;
    @ColumnWidth(5)
    @ExcelProperty("typeLine")
    private int typeLine;
    @ColumnWidth(4)
    @ExcelProperty("memberLine")
    private int memberLine;
    @ExcelProperty("typeName")
    private String typeName;
    @ExcelProperty("memberName")
    private String memberName;

    @ColumnWidth(25)
    @ExcelProperty("comment1")
    private String comment1;
    @ColumnWidth(25)
    @ExcelProperty("comment2")
    private String comment2;
    @ColumnWidth(25)
    @ExcelProperty("comment3")
    private String comment3;

    /** 类类型-用于筛选 */
    @ColumnWidth(8)
    @ExcelProperty("Type")
    private String type;
    /** 成员类型-用于筛选 */
    @ColumnWidth(8)
    @ExcelProperty("memberType")
    private String memberType;

    @ColumnWidth(8)
    @ExcelProperty("modifiers")
    private String modifiers;
    @ColumnWidth(25)
    @ExcelProperty("returnType")
    private String returnType;
    @ColumnWidth(15)
    @ExcelProperty("paramTypeNames")
    private String paramTypeNames;

    /** 首字母小写类名-用于语句 */
    @ColumnWidth(10)
    @ExcelProperty("lowFirstType")
    private String lowFirstType;
    /** 参数名列表-用于语句 最后便于查看 */
    @ColumnWidth(25)
    @ExcelProperty("paramNames")
    private String paramNames;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getTypeLine() {
        return typeLine;
    }

    public void setTypeLine(int typeLine) {
        this.typeLine = typeLine;
    }

    public int getMemberLine() {
        return memberLine;
    }

    public void setMemberLine(int memberLine) {
        this.memberLine = memberLine;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getComment1() {
        return comment1;
    }

    public void setComment1(String comment1) {
        this.comment1 = comment1;
    }

    public String getComment2() {
        return comment2;
    }

    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    public String getComment3() {
        return comment3;
    }

    public void setComment3(String comment3) {
        this.comment3 = comment3;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getModifiers() {
        return modifiers;
    }

    public void setModifiers(String modifiers) {
        this.modifiers = modifiers;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getParamTypeNames() {
        return paramTypeNames;
    }

    public void setParamTypeNames(String paramTypeNames) {
        this.paramTypeNames = paramTypeNames;
    }

    public String getLowFirstType() {
        return lowFirstType;
    }

    public void setLowFirstType(String lowFirstType) {
        this.lowFirstType = lowFirstType;
    }

    public String getParamNames() {
        return paramNames;
    }

    public void setParamNames(String paramNames) {
        this.paramNames = paramNames;
    }
}
