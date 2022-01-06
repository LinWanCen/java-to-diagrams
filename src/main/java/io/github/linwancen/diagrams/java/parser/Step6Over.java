package io.github.linwancen.diagrams.java.parser;

import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
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
            Optional<ResolvedReferenceTypeDeclaration> optional = parent.getTypeDeclaration();
            if (!optional.isPresent()) {
                continue;
            }
            ResolvedReferenceTypeDeclaration parentRt = optional.get();
            String parentTypeSign = InfoUtils.resolvedTypeSign(parentRt);
            if (parentTypeSign.startsWith("java")) {
                continue;
            }
            TypeInfo parentTypeInfo = typeMap.get(parentTypeSign);
            if (parentTypeInfo == null) {
                parentTypeInfo = new TypeInfo();
                InfoUtils.addResolvedTypeInfo(parentTypeInfo, parentRt);
                typeMap.put(parentTypeSign, parentTypeInfo);
                if (overTypeInfo.comment == null && parentTypeInfo.comment != null) {
                    overTypeInfo.comment = parentTypeInfo.comment;
                    overTypeInfo.commentLines = parentTypeInfo.commentLines;
                }
            }
            if (isImpl) {
                overTypeInfo.faceInfo.put(parentTypeInfo.sign, parentTypeInfo);
                parentTypeInfo.implInfo.put(overTypeInfo.sign, overTypeInfo);
            } else {
                overTypeInfo.parentInfo = parentTypeInfo;
                parentTypeInfo.childInfo.put(overTypeInfo.sign, overTypeInfo);
            }
            for (MethodUsage usage : parent.getDeclaredMethods()) {
                ResolvedMethodDeclaration parentMethod = usage.getDeclaration();
                // 私有方法不会被重写
                if (AccessSpecifier.PRIVATE == parentMethod.accessSpecifier()) {
                    continue;
                }
                // 查找当前类是否有同签名的重写方法
                ResolvedMethodDeclaration overMethod = null;
                for (ResolvedMethodDeclaration m : type.resolve().getDeclaredMethods()) {
                    if (parentMethod.getSignature().equals(m.getSignature())) {
                        overMethod = m;
                        break;
                    }
                }
                if (overMethod == null) {
                    // TODO 跨级串接
                    continue;
                }
                // 重写的方法
                MemberInfo overInfo = InfoFactory.getOrCreateMethodInfo(overTypeInfo, overMethod);
                // 父类或实现类方法
                MemberInfo parentInfo = InfoFactory.getOrCreateMethodInfo(parentTypeInfo, parentMethod);
                if (overInfo.comment == null && parentInfo.comment != null) {
                    overInfo.comment = parentInfo.comment;
                    overInfo.commentLines = parentInfo.commentLines;
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
