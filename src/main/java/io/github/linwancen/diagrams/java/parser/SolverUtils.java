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
import java.util.HashSet;
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
     * 添加所属源文件根目录
     */
    static void addSolverParentSrcDir(CombinedTypeSolver solver, Set<String> addSrc, String srcPath) {
        if (addSrc.add(srcPath)) {
            addSolverDir(solver, srcPath);
        }
    }

    /**
     * 添加源文件根目录
     */
    static void addSolverDir(CombinedTypeSolver solver, String srcRootDir) {
        LOG.info("addSolverDir\t{}", srcRootDir);
        long startTime = System.nanoTime();

        solver.add(new JavaParserTypeSolver(srcRootDir));

        long useTime = (System.nanoTime() - startTime) / 1000000;
        LOG.info("addSolverDir success, use {}ms", useTime);
    }

    /**
     * 添加用于解析的 Maven jar 包
     */
    static void addSolverMavenJars(CombinedTypeSolver solver, HashSet<String> addJars, File file) {
        LOG.info("get Maven Dep\t{}", file.getAbsolutePath());
        long startTime = System.nanoTime();

        String dep = MavenUtils.getDep(file);

        long useTime = (System.nanoTime() - startTime) / 1000000;
        LOG.info("get Maven Dep success, use {}ms", useTime);

        if (dep != null && addJars.add(dep)) {
            addSolverJars(solver, dep);
        }
    }

    /**
     * 添加用于解析的 jar 包
     * <br>路径获取命令：<br>
     * mvn dependency:build-classpath
     */
    static void addSolverJars(CombinedTypeSolver solver, String pathToJars) {
        LOG.info("addSolverJars\t{}", pathToJars);
        long startTime = System.nanoTime();

        JarTypeSolver jarTypeSolver;
        String[] pathToJarArr = FileUtils.split(pathToJars);
        for (String pathToJar : pathToJarArr) {
            if (File.separatorChar == '\\') {
                pathToJar = pathToJar.replace('/', '\\');
            }
            try {
                jarTypeSolver = new JarTypeSolver(pathToJar);
                solver.add(jarTypeSolver);
            } catch (IOException e) {
                LOG.error("addSolverJars fail, pathToJar:\n{}\n", pathToJar, e);
            }
        }

        long useTime = (System.nanoTime() - startTime) / 1000000;
        LOG.info("addSolverJars success, use {}ms", useTime);
    }
}
