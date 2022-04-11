package io.github.linwancen.util.maven;

import org.apache.maven.shared.invoker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class MavenUtils {

    private MavenUtils() {}

    private static final Logger LOG = LoggerFactory.getLogger(MavenUtils.class);

    static {
        // 兼容环境变量不是 M2_HOME 而是 MAVEN_HOME
        String mavenHome = System.getenv("MAVEN_HOME");
        if (mavenHome != null) {
            System.setProperty("maven.home", mavenHome);
        }
    }

    /**
     * 获取依赖 jar
     */
    public static List<String> getDep(File pomFile) {
        DepHandler handler = new DepHandler();
        run(pomFile, handler, Collections.singletonList("dependency:build-classpath"));
        return handler.output;
    }

    /**
     * 向上查找 pom 文件
     */
    public static File parentPomFile(File path) {
        // 避免相对路径 getParentFile 为空
        path = path.getAbsoluteFile();
        File pomFile = new File(path, "pom.xml");
        while (!pomFile.exists()) {
            path = path.getParentFile();
            if (path == null) {
                return null;
            }
            pomFile = new File(path, "pom.xml");
        }
        return pomFile;
    }

    /**
     * 执行 Maven 命令
     */
    public static void run(File pomFile, InvocationOutputHandler outputHandler, List<String> goals) {
        if (pomFile == null || goals == null || goals.isEmpty()) {
            return;
        }
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(pomFile);
        request.setGoals(goals);
        request.setOutputHandler(outputHandler);
        request.setBatchMode(true);
        Invoker invoker = new DefaultInvoker();
        try {
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            LOG.error("pomFile:\n{}", goals);
            LOG.error("goals fail:\t{}", goals, e);
        }
    }
}
