package io.github.linwancen.diagrams.java.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import io.github.linwancen.diagrams.Conf;
import io.github.linwancen.diagrams.java.api.JavaParse;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;
import io.github.linwancen.util.java.FileUtils;
import io.github.linwancen.util.java.FilterUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 步骤 1 目录
 */
public class Step1Dirs {

    private Step1Dirs() {}

    /**
     * 解析多个文件或目录
     * 用可变参数而不是 List 是因为 {@link File#listFiles(FileFilter)} 是数组
     */
    public static void parseDirs(List<JavaParse> javaParses, File... files) {

        Pattern includePath = Pattern.compile(Conf.DIAGRAMS_PATH_INCLUDE.get());
        Pattern excludePath = Pattern.compile(Conf.DIAGRAMS_PATH_EXCLUDE.get());

        CombinedTypeSolver solver = new CombinedTypeSolver();
        // 类加载器必须添加以便获得 java 的
        solver.add(new ClassLoaderTypeSolver(ClassLoader.getSystemClassLoader()));
        // 加载设置的解析资源
        String src = Conf.DIAGRAMS_SOLVER_SRC.get();
        if (src.length() > 0) {
            for (String s : FileUtils.split(src)) {
                SolverUtils.addSolverDir(solver, s);
            }
        }
        String jar = Conf.DIAGRAMS_SOLVER_JAR.get();
        if (jar.length() > 0) {
            SolverUtils.addSolverJars(solver, jar);
        }
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(solver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        LinkedHashMap<String, TypeInfo> typeMap = new LinkedHashMap<>();
        LinkedHashMap<String, String> packComment = new LinkedHashMap<>();
        HashSet<String> addJars = new HashSet<>();
        HashSet<String> addSrc = new HashSet<>();
        for (File file : files) {
            if ("true".equals(Conf.DIAGRAMS_SOLVER_AUTO_JAR.get())) {
                SolverUtils.addSolverMavenJars(solver, addJars, file);
            }
            FileUtils.deep(
                    f -> {
                        if (!"package-info.java".equals(f.getName())) {
                            Step2File.parseFile(typeMap, javaParses, packComment, f, solver, addSrc);
                        }
                    },
                    f -> {
                        if (f.isDirectory()) {
                            File packageInfo = new File(f, "package-info.java");
                            if (packageInfo.exists()) {
                                Step2File.parseFile(typeMap, javaParses, packComment, packageInfo, solver, addSrc);
                            }
                            return true;
                        }
                        return FilterUtils.filter(FileUtils.canonicalPath(f), includePath, excludePath);
                    },
                    file);
        }
        javaParses.forEach(v -> v.typeMap(typeMap));
    }
}
