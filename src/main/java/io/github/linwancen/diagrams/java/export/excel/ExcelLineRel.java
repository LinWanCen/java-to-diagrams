package io.github.linwancen.diagrams.java.export.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;

/**
 * EasyExcel 关系行对象
 */
@SuppressWarnings("unused")
@HeadStyle(wrapped = BooleanEnum.FALSE, horizontalAlignment = HorizontalAlignmentEnum.LEFT)
@HeadFontStyle(fontHeightInPoints = 11, bold = BooleanEnum.FALSE, fontName="宋体")
public class ExcelLineRel {
    @ExcelProperty("callOrOver")
    private String callOrOver;
    @ExcelProperty("callOrOverType")
    private String callOrOverType;
    @ExcelProperty("callOrOverComment1")
    private String callOrOverComment1;
    @ExcelProperty("memberName")
    private String memberName;
    @ExcelProperty("typeName")
    private String typeName;
    @ExcelProperty("comment1")
    private String comment1;
    @ExcelProperty("rel")
    private String rel;

    public String getCallOrOver() {
        return callOrOver;
    }

    public void setCallOrOver(String callOrOver) {
        this.callOrOver = callOrOver;
    }

    public String getCallOrOverType() {
        return callOrOverType;
    }

    public void setCallOrOverType(String callOrOverType) {
        this.callOrOverType = callOrOverType;
    }

    public String getCallOrOverComment1() {
        return callOrOverComment1;
    }

    public void setCallOrOverComment1(String callOrOverComment1) {
        this.callOrOverComment1 = callOrOverComment1;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getComment1() {
        return comment1;
    }

    public void setComment1(String comment1) {
        this.comment1 = comment1;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }
}
