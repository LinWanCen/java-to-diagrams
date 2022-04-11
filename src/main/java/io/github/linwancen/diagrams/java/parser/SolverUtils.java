package io.github.linwancen.diagrams.java.parser;

import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import io.github.linwancen.util.java.FileUtils;
import io.github.linwancen.util.maven.MavenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 解析器工具类
 */
class SolverUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SolverUtils.class);

    private SolverUtils() {}

    /**
     * 源码目录
     */
    static String srcPath(File file, int packNum) {
        File src = file.getParentFile();
        for (int i = 0; i < packNum; i++) {
            src = src.getParentFile();
        }
        return src.getAbsolutePath();
    }

    /**
     * 添加源文件根目录
     */
    static void addSolverDir(CombinedTypeSolver solver, String srcDir, Set<String> addSrc) {
        if (addSrc.add(srcDir)) {
            String srcPath = srcDir.replace('\\', '/');
            LOG.info("addSolverDir\tfile:///{}", srcPath);
            solver.add(new JavaParserTypeSolver(srcPath));
        }
    }

    /**
     * 添加用于解析的 Maven jar 包
     */
    static void addSolverMavenJars(CombinedTypeSolver solver, File file, Set<String> addPoms, Set<String> addJars) {
        File pomFile = MavenUtils.parentPomFile(file);
        if (pomFile == null) {
            return;
        }
        String pomFilePath = FileUtils.canonicalPath(pomFile);
        if (!addPoms.add(pomFilePath)) {
            return;
        }

        LOG.info("get Maven Dep\tfile:///{}", pomFilePath);
        long startTime = System.nanoTime();

        List<String> depList = MavenUtils.getDep(pomFile);

        long useTime = (System.nanoTime() - startTime) / 1000000;
        LOG.info("get Maven Dep success, use {}ms", useTime);

        for (String dep : depList) {
            LOG.info("addSolverJars\t{}", dep);
            addSolverJars(solver, dep, addJars);
        }
    }

    /**
     * 添加用于解析的 jar 包
     * <br>路径获取命令：<br>
     * mvn dependency:build-classpath
     */
    static void addSolverJars(CombinedTypeSolver solver, String pathToJars, Set<String> addJars) {
        String[] pathToJarArr = FileUtils.split(pathToJars);
        for (String pathToJar : pathToJarArr) {
            if (File.separatorChar == '\\') {
                pathToJar = pathToJar.replace('/', '\\');
            }
            if (!addJars.add(pathToJar)) {
                continue;
            }
            try {
                solver.add(new JarTypeSolver(pathToJar));
            } catch (IOException e) {
                LOG.error("addSolverJars fail, pathToJar:\n{}\n", pathToJar, e);
            }
        }
    }
}
