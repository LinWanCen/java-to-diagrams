package io.github.linwancen.diagrams.java.parser;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import io.github.linwancen.diagrams.java.api.JavaParse;
import io.github.linwancen.diagrams.java.api.bean.MemberInfo;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;
import io.github.linwancen.diagrams.java.api.dict.AccessEnum;
import io.github.linwancen.diagrams.java.api.dict.MemberEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 步骤 4 成员
 */
class Step4Members {

    private static final Logger LOG = LoggerFactory.getLogger(Step4Members.class);

    private Step4Members() {}

    /**
     * 遍历类解析成员
     */
    static void parseMembers(LinkedHashMap<String, TypeInfo> typeMap,
                             List<JavaParse> javaParses,
                             TypeDeclaration<?> type,
                             List<String> packNames,
                             List<String> packComments, ResolvedReferenceTypeDeclaration rt,
                             TypeInfo typeInfo) {
        for (BodyDeclaration<?> m : type.getMembers()) {
            MemberInfo info;
            if (m.isMethodDeclaration()) {
                MethodDeclaration d = m.asMethodDeclaration();
                ResolvedMethodDeclaration r = d.resolve();
                String sign = InfoUtils.methodSign(r);
                info = typeInfo.memberInfo.computeIfAbsent(sign, s -> new MemberInfo());
                // 为了修正接口方法默认共有所以先传
                info.typeInfo = typeInfo;
                InfoUtils.addResolvedMethodInfo(info, r);
                InfoUtils.addMethodInfo(info, d);
                info.memberType = MemberEnum.METHOD;
                InfoUtils.isGetSet(info, type, rt);
            } else if (m.isConstructorDeclaration()) {
                ConstructorDeclaration d = m.asConstructorDeclaration();
                ResolvedConstructorDeclaration r = d.resolve();
                String sign = InfoUtils.methodSign(r);
                info = typeInfo.memberInfo.computeIfAbsent(sign, s -> new MemberInfo());
                // 为了修正接口方法默认共有所以先传
                info.typeInfo = typeInfo;
                InfoUtils.addResolvedMethodInfo(info, r);
                InfoUtils.addMethodInfo(info, d);
                info.memberType = MemberEnum.CONSTRUCTOR;
            } else if (m.isFieldDeclaration()) {
                FieldDeclaration d = m.asFieldDeclaration();
                ResolvedFieldDeclaration r = d.resolve();
                String sign = typeInfo.sign + "." + r.getName();
                info = typeInfo.memberInfo.computeIfAbsent(sign, s -> new MemberInfo());
                info.sign = sign;
                InfoUtils.addResolvedFieldInfo(info, r);
                InfoUtils.addFieldInfo(info, d);
                info.memberType = MemberEnum.FIELD;
            } else if (m.isInitializerDeclaration()) {
                InitializerDeclaration d = m.asInitializerDeclaration();
                String sign = typeInfo.sign + "_static";
                info = typeInfo.memberInfo.computeIfAbsent(sign, s -> new MemberInfo());
                info.sign = sign;
                info.name = typeInfo.name + "_static";
                info.isStatic = d.isStatic();
                info.access = AccessEnum.NONE;
                info.memberType = MemberEnum.STATIC;
            } else if (m.isTypeDeclaration()) {
                // 回到类解析循环调用
                Step3Type.parseType(typeMap, javaParses, m.asTypeDeclaration(), packNames, packComments);
                continue;
            } else {
                LOG.warn("skip: {}", m);
                continue;
            }
            info.isSelect = true;
            info.typeInfo = typeInfo;
            javaParses.forEach(v -> v.member(info));

            Step5MethodCall.parseMethodCall(typeMap, javaParses, m, typeInfo, info);
        }
    }
}
