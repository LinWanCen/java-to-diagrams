package io.github.linwancen.diagrams.java.export.excel;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import io.github.linwancen.diagrams.Conf;
import io.github.linwancen.diagrams.java.api.JavaParse;
import io.github.linwancen.diagrams.java.api.bean.JavaInfo;
import io.github.linwancen.diagrams.java.api.bean.MemberInfo;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;
import io.github.linwancen.diagrams.java.api.dict.MemberEnum;
import io.github.linwancen.util.excel.FreezeAndFilter;
import io.github.linwancen.util.java.FilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

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

    /**
     * 类和方法共同的注释信息
     */
    private ExcelLine excelLineFrom(JavaInfo javaInfo) {
        ExcelLine line = new ExcelLine();
        line.setComment1(javaInfo.getComment(0));
        line.setComment2(javaInfo.getComment(1));
        line.setComment3(javaInfo.getComment(2));
        return line;
    }

    /**
     * 类和方法共同添加的类信息
     */
    private void addTypeInfo(ExcelLine line, TypeInfo typeInfo) {
        line.setTypeName(typeInfo.name);
        line.setModifiers(typeInfo.modStr());
        line.setLowFirstType(typeInfo.lowFirstName);
        line.setType(typeInfo.type.string);
        line.setAuthor(typeInfo.getAuthor());
        line.setTypeLine(typeInfo.lineCount);
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
        addTypeInfo(line, typeInfo);
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
        addTypeInfo(line, info.typeInfo);
        line.setMemberLine(info.lineCount);
        line.setMemberName(info.name);
        line.setModifiers(info.modStr());
        line.setReturnType(info.returnType);
        line.setMemberType(info.memberType.string);
        if (info.memberType == MemberEnum.METHOD) {
            line.setParamNames(info.paramNamesStr());
            line.setParamTypeNames(info.paramNamesTypeStr());
            writer.write(Collections.singletonList(line), methodSheet);
        } else if (info.memberType == MemberEnum.FIELD) {
            writer.write(Collections.singletonList(line), fieldSheet);
        }
    }

    WriteSheet relSheet = EasyExcelFactory
            .writerSheet(4, "rel")
            .head(ExcelLineRel.class)
            .registerWriteHandler(new FreezeAndFilter())
            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
            .build();

    private final Pattern includeMethod = Pattern.compile(Conf.DIAGRAMS_XMIND_METHOD_INCLUDE.get());
    private final Pattern excludeMethod = Pattern.compile(Conf.DIAGRAMS_XMIND_METHOD_EXCLUDE.get());

    /**
     * 写入关系行
     */
    private void writeRelLine(String rel, MemberInfo call, MemberInfo usage) {
        // 跳过这些无关紧要的
        if (usage.memberType == MemberEnum.STATIC || usage.memberType == MemberEnum.FIELD || call.memberType == MemberEnum.GET_SET) {
            return;
        }
        if (!FilterUtils.filter(call.sign, includeMethod, excludeMethod)) {
            return;
        }
        ExcelLineRel line = new ExcelLineRel();
        line.setRelType(rel);

        line.setCallSign(call.sign);
        line.setUsageSign(usage.sign);
        writer.write(Collections.singletonList(line), relSheet);
    }

    @Override
    public void call(MemberInfo usage, MemberInfo call) {
        writeRelLine("call", call, usage);
    }

    @Override
    public void extend(MemberInfo child, MemberInfo parent) {
        writeRelLine("extend", child, parent);
    }

    @Override
    public void impl(MemberInfo impl, MemberInfo face) {
        writeRelLine("impl", impl, face);
    }

    @Override
    public void typeMap(LinkedHashMap<String, TypeInfo> typeMap) {
        // 空实现
    }

    @Override
    public void end() {
        writer.finish();
        String path = outFile.getAbsolutePath().replace('\\', '/');
        LOG.info("Excel 表格：\tfile:///{}", path);
    }
}
