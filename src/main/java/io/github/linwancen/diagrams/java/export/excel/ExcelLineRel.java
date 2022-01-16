package io.github.linwancen.diagrams.java.export.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;

/**
 * EasyExcel 关系行
 */
@SuppressWarnings("unused")
@HeadStyle(wrapped = BooleanEnum.FALSE, horizontalAlignment = HorizontalAlignmentEnum.LEFT)
@HeadFontStyle(fontHeightInPoints = 11, bold = BooleanEnum.FALSE, fontName="宋体")
public class ExcelLineRel {
    @ExcelProperty("relType")
    private String relType;

    @ColumnWidth(25)
    @ExcelProperty("callSign")
    private String callSign;
    @ColumnWidth(25)
    @ExcelProperty("usageSign")
    private String usageSign;

    public String getRelType() {
        return relType;
    }

    public void setRelType(String relType) {
        this.relType = relType;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public String getUsageSign() {
        return usageSign;
    }

    public void setUsageSign(String usageSign) {
        this.usageSign = usageSign;
    }
}
