package io.github.linwancen.diagrams.java.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import io.github.linwancen.diagrams.Conf;
import io.github.linwancen.diagrams.java.api.JavaParse;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;
import io.github.linwancen.util.java.CommentUtils;
import io.github.linwancen.util.java.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * 步骤 2 文件
 */
class Step2File {

    private static final Logger LOG = LoggerFactory.getLogger(Step2File.class);

    private Step2File() {}

    /**
     * 解析文件
     */
    static void parseFile(LinkedHashMap<String, TypeInfo> typeMap,
                          List<JavaParse> javaParses,
                          LinkedHashMap<String, String> packCommentMap,
                          File file,
                          CombinedTypeSolver solver,
                          Set<String> addPoms,
                          Set<String> addJars,
                          Set<String> addSrc
    ) {
        CompilationUnit cu = StaticJavaParser.parse(FileUtils.read(file));
        List<String> packNames = new ArrayList<>();
        List<String> packComments = new ArrayList<>();
        cu.getPackageDeclaration().ifPresent(e -> e.getChildNodes().get(0).stream()
                .map(Objects::toString)
                .forEach(packageName -> {
                    packNames.add(packageName);
                    packComments.add(packCommentMap.get(packageName));
                }));
        String srcPath = SolverUtils.srcPath(file, packNames.size());
        String filePath = file.getAbsolutePath().substring(srcPath.length() + 1);
        LOG.info("parseFile\t{}", filePath);
        if ("package-info.java".equals(file.getName())) {
            cu.getComment().ifPresent(comment -> {
                String packageName = packNames.get(0);
                String doc = StaticJavaParser.parseJavadoc(comment.getContent()).getDescription().toText();
                packCommentMap.put(packageName, CommentUtils.textFrom(doc));
            });
        }
        if ("true".equals(Conf.DIAGRAMS_SOLVER_AUTO_SRC.get())) {
            SolverUtils.addSolverDir(solver, srcPath, addSrc);
        }
        if ("true".equals(Conf.DIAGRAMS_SOLVER_AUTO_JAR_EACH.get())) {
            SolverUtils.addSolverMavenJars(solver, file, addPoms, addJars);
        }
        for (TypeDeclaration<?> type : cu.getTypes()) {
            Step3Type.parseType(typeMap, javaParses, type, packNames, packComments);
        }
    }
}
