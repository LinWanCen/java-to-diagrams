package io.github.linwancen.diagrams;

import io.github.linwancen.diagrams.java.api.JavaParse;
import io.github.linwancen.diagrams.java.export.excel.JavaParseImplExcel;
import io.github.linwancen.diagrams.java.export.graphviz.JavaParseImplGraphviz;
import io.github.linwancen.diagrams.java.export.graphviz.JavaParseImplGraphvizClass;
import io.github.linwancen.diagrams.java.export.xmind.JavaParseImplXMindCall;
import io.github.linwancen.diagrams.java.export.xmind.JavaParseImplXMindClassCall;
import io.github.linwancen.diagrams.java.export.xmind.JavaParseImplXMindTree;
import io.github.linwancen.diagrams.java.parser.Step1Dirs;
import io.github.linwancen.util.java.ConfUtils;
import io.github.linwancen.util.java.FileUtils;
import io.github.linwancen.util.java.JFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 主类
 */
public class JavaParserMain {

    private static final Logger LOG = LoggerFactory.getLogger(JavaParserMain.class);

    /**
     * 读取参数、配置、环境或弹框选择
     */
    public static void main(String... args) {
        Conf.prop = ConfUtils.loadUtf8(Conf.confPath);
        if (args.length == 0) {
            String path = Conf.DIAGRAMS_PATH.get();
            if (path != null) {
                args = new String[]{path};
            }
        }
        if (args.length == 1) {
            args = FileUtils.split(args[0]);
        }
        if (args.length > 0) {
            File[] files = Arrays.stream(args).map(File::new).toArray(File[]::new);
            run(files);
            return;
        }
        File[] files = JFileUtils.chooser();
        if (files.length == 0) {
            LOG.warn("未选择需要解析的文件！");
            return;
        }
        run(files);
    }

    /**
     * 组装生成器
     */
    protected static void run(File... files) {
        String outName;
        if (files.length == 1 || files[0].isFile()) {
            outName = files[0].getAbsoluteFile().getName();
        } else {
            outName = Arrays.stream(files)
                    .map(File::getAbsoluteFile)
                    .map(File::getName)
                    .collect(Collectors.joining("+"));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("_yyyy-MM-dd_HH.mm.ss");
        outName += sdf.format(new Date());
        outName += Conf.DIAGRAMS_OUT_SUFFIX.get();

        File outDir = new File(FileUtils.CLASS_PATH, outName);
        FileUtils.mkdir(outDir);

        List<JavaParse> parsers = new ArrayList<>();
        add(parsers, Conf.DIAGRAMS_XMIND_TREE, new JavaParseImplXMindTree(outDir, outName));
        add(parsers, Conf.DIAGRAMS_XMIND_CALL, new JavaParseImplXMindCall(outDir, outName));
        add(parsers, Conf.DIAGRAMS_XMIND_CLASS_CALL, new JavaParseImplXMindClassCall(outDir, outName));
        add(parsers, Conf.DIAGRAMS_DOT, new JavaParseImplGraphviz(outDir, outName));
        add(parsers, Conf.DIAGRAMS_DOT_CLASS, new JavaParseImplGraphvizClass(outDir, outName));
        add(parsers, Conf.DIAGRAMS_EXCEL, new JavaParseImplExcel(outDir, outName));

        Step1Dirs.parseDirs(parsers, files);

        for (JavaParse parser : parsers) {
            parser.end();
        }
    }

    /**
     * 按配置添加生成器
     */
    private static void add(List<JavaParse> parsers, Conf conf, JavaParse javaParse) {
        if ("true".equals(conf.get())) {
            parsers.add(javaParse);
        }
    }
}
