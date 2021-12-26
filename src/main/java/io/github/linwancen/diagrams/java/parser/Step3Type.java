package io.github.linwancen.diagrams.java.parser;

import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedClassDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedInterfaceDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import io.github.linwancen.diagrams.java.api.JavaParse;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 步骤 3 类
 */
class Step3Type {

    private Step3Type() {}

    /**
     * 解析类
     */
    static void parseType(LinkedHashMap<String, TypeInfo> typeMap,
                          List<JavaParse> javaParses,
                          TypeDeclaration<?> type,
                          List<String> packNames, List<String> packComments) {
        if (type.isAnnotationDeclaration()) {
            return;
        }
        ResolvedReferenceTypeDeclaration rt = type.resolve();
        String sign = InfoUtils.typeSign(type);
        TypeInfo typeInfo = typeMap.computeIfAbsent(sign, s -> new TypeInfo());
        typeInfo.isSelect = true;
        typeInfo.packNames = packNames;
        typeInfo.packComments = packComments;
        InfoUtils.addTypeInfo(typeInfo, type);
        javaParses.forEach(v -> v.type(typeInfo));

        // 先执行以便复制注释
        if (type.isClassOrInterfaceDeclaration()) {
            if (rt.isClass()) {
                ResolvedClassDeclaration rcd = rt.asClass();
                Step6Over.parseOver(typeMap, javaParses, type, rcd.getAllInterfaces(), typeInfo, true);
                Step6Over.parseOver(typeMap, javaParses, type, rcd.getAllSuperClasses(), typeInfo, false);
            }
            if (rt.isInterface()) {
                ResolvedInterfaceDeclaration rid = rt.asInterface();
                Step6Over.parseOver(typeMap, javaParses, type, rid.getAllInterfacesExtended(), typeInfo, false);
            }
        }

        Step4Members.parseMembers(typeMap, javaParses, type, packNames, packComments, rt, typeInfo);
    }
}
