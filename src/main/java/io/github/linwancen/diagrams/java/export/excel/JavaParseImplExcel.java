package io.github.linwancen.diagrams.java.export.excel;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import io.github.linwancen.diagrams.java.api.JavaParse;
import io.github.linwancen.diagrams.java.api.bean.JavaInfo;
import io.github.linwancen.diagrams.java.api.bean.MemberInfo;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;
import io.github.linwancen.diagrams.java.api.dict.MemberEnum;
import io.github.linwancen.util.excel.FreezeAndFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * Excel 实现
 * <br>测试用标签换行
 * 不用<br>换行
 */
public class JavaParseImplExcel implements JavaParse {

    private static final Logger LOG = LoggerFactory.getLogger(JavaParseImplExcel.class);

    public final File outFile;
    public final ExcelWriter writer;

    public JavaParseImplExcel(File outDir, String outName) {
        outFile = new File(outDir, outName + ".xlsx");
        writer = EasyExcelFactory.write(outFile).build();
    }

    private ExcelLine excelLineFrom(JavaInfo typeInfo) {
        ExcelLine line = new ExcelLine();
        line.setComment1(typeInfo.getComment(0));
        line.setComment2(typeInfo.getComment(1));
        line.setComment3(typeInfo.getComment(2));
        line.setLineCount(typeInfo.lineCount);
        return line;
    }

    WriteSheet typeSheet = EasyExcelFactory
            .writerSheet(1, "type")
            .head(ExcelLine.class)
            .registerWriteHandler(new FreezeAndFilter())
            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
            .build();

    @Override
    public void type(TypeInfo typeInfo) {
        ExcelLine line = excelLineFrom(typeInfo);
        line.setTypeName(typeInfo.name);
        line.setAuthor(typeInfo.getAuthor());
        writer.write(Collections.singletonList(line), typeSheet);
    }

    WriteSheet methodSheet = EasyExcelFactory
            .writerSheet(2, "method")
            .head(ExcelLine.class)
            .registerWriteHandler(new FreezeAndFilter())
            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
            .build();

    WriteSheet fieldSheet = EasyExcelFactory
            .writerSheet(3, "field")
            .head(ExcelLine.class)
            .registerWriteHandler(new FreezeAndFilter())
            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
            .build();

    @Override
    public void member(MemberInfo info) {
        ExcelLine line = excelLineFrom(info);
        line.setMemberName(info.name);
        line.setTypeName(info.typeInfo.name);
        line.setAuthor(info.typeInfo.getAuthor());
        if (info.memberType == MemberEnum.METHOD) {
            writer.write(Collections.singletonList(line), methodSheet);
        } else if (info.memberType == MemberEnum.FIELD) {
            writer.write(Collections.singletonList(line), fieldSheet);
        }
    }

    private ExcelLineRel excelLineRelFrom(MemberInfo callOrOver, MemberInfo info, String rel) {
        ExcelLineRel line = new ExcelLineRel();
        line.setCallOrOver(callOrOver.name);
        line.setCallOrOverType(callOrOver.typeInfo.sign);
        line.setCallOrOverComment1(callOrOver.getComment(0));
        line.setMemberName(info.name);
        line.setTypeName(info.typeInfo.sign);
        line.setComment1(info.getComment(0));
        line.setRel(rel);
        return line;
    }

    WriteSheet relSheet = EasyExcelFactory
            .writerSheet(4, "rel")
            .head(ExcelLineRel.class)
            .registerWriteHandler(new FreezeAndFilter())
            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
            .build();

    @Override
    public void call(MemberInfo usage, MemberInfo call) {
        // 跳过这些无关紧要的
        if (usage.memberType == MemberEnum.STATIC || usage.memberType == MemberEnum.FIELD || call.memberType == MemberEnum.GET_SET) {
            return;
        }
        ExcelLineRel line = excelLineRelFrom(call, usage, "call");
        writer.write(Collections.singletonList(line), relSheet);
    }

    @Override
    public void extend(MemberInfo child, MemberInfo parent) {
        ExcelLineRel line = excelLineRelFrom(child, parent, "extend");
        writer.write(Collections.singletonList(line), relSheet);
    }

    @Override
    public void impl(MemberInfo impl, MemberInfo face) {
        ExcelLineRel line = excelLineRelFrom(impl, face, "impl");
        writer.write(Collections.singletonList(line), relSheet);
    }

    @Override
    public void typeMap(LinkedHashMap<String, TypeInfo> typeMap) {

    }

    @Override
    public void end() {
        writer.finish();
        String path = outFile.getAbsolutePath().replace('\\', '/');
        LOG.info("Excel 表格：\tfile:///{}", path);
    }
}
