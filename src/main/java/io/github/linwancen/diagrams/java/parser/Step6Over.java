package io.github.linwancen.diagrams.java.parser;

import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import io.github.linwancen.diagrams.java.api.JavaParse;
import io.github.linwancen.diagrams.java.api.bean.MemberInfo;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * 步骤 6 重写
 */
class Step6Over {

    private Step6Over() {}

    /**
     * 解析实现或继承
     */
    static void parseOver(LinkedHashMap<String, TypeInfo> typeMap,
                          List<JavaParse> javaParses,
                          TypeDeclaration<?> type,
                          List<ResolvedReferenceType> parents,
                          TypeInfo overTypeInfo,
                          boolean isImpl) {
        for (ResolvedReferenceType parent : parents) {
            Optional<ResolvedReferenceTypeDeclaration> ort = parent.getTypeDeclaration();
            if (!ort.isPresent()) {
                continue;
            }
            ResolvedReferenceTypeDeclaration rt = ort.get();
            String parentTypeSign = InfoUtils.resolvedTypeSign(rt);
            TypeInfo parentTypeInfo = typeMap.get(parentTypeSign);
            if (parentTypeInfo == null) {
                parentTypeInfo = new TypeInfo();
                typeMap.put(parentTypeSign, parentTypeInfo);
                if (overTypeInfo.comment == null && parentTypeInfo.comment != null) {
                    overTypeInfo.comment = parentTypeInfo.comment;
                    overTypeInfo.commentFirst = parentTypeInfo.commentFirst;
                }
            }
            if (isImpl) {
                overTypeInfo.faceInfo.put(parentTypeInfo.sign, parentTypeInfo);
                parentTypeInfo.implInfo.put(overTypeInfo.sign, overTypeInfo);
            } else {
                overTypeInfo.parentInfo = parentTypeInfo;
                parentTypeInfo.childInfo.put(overTypeInfo.sign, overTypeInfo);
            }
            for (MethodUsage dm : parent.getDeclaredMethods()) {
                ResolvedMethodDeclaration r = dm.getDeclaration();
                // 私有方法不会被重写
                if (AccessSpecifier.PRIVATE == r.accessSpecifier()) {
                    continue;
                }
                // 查找当前类是否有同签名的重写方法
                String[] paramTypes = dm.getParamTypes().stream()
                        .map(ResolvedType::describe)
                        .map(s -> s.substring(s.lastIndexOf(".") + 1))
                        .toArray(String[]::new);
                List<MethodDeclaration> methods = type.getMethodsBySignature(dm.getName(), paramTypes);
                if (methods.isEmpty()) {
                    // TODO 跨级串接
                    continue;
                }
                // 重写的方法
                MemberInfo overInfo = InfoFactory.getOrCreateMemberInfo(overTypeInfo, methods.get(0).resolve());
                // 父类或实现类方法
                MemberInfo parentInfo = InfoFactory.getOrCreateMemberInfo(parentTypeInfo, r);
                if (overInfo.comment == null && parentInfo.comment != null) {
                    overInfo.comment = parentInfo.comment;
                    overInfo.commentFirst = parentInfo.commentFirst;
                }
                if (isImpl) {
                    javaParses.forEach(v -> v.impl(overInfo, parentInfo));
                    overInfo.faceInfo.put(parentInfo.sign, parentInfo);
                    parentInfo.implInfo.put(overInfo.sign, overInfo);
                } else {
                    javaParses.forEach(v -> v.extend(overInfo, parentInfo));
                    overInfo.parentInfo = parentInfo;
                    parentInfo.childInfo.put(overInfo.sign, overInfo);
                }
            }
        }
    }
}
