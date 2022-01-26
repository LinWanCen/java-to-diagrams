package io.github.linwancen.diagrams.java.parser;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import io.github.linwancen.diagrams.java.api.JavaParse;
import io.github.linwancen.diagrams.java.api.bean.MemberInfo;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 步骤 5 调用
 */
class Step5MethodCall {

    private static final Logger LOG = LoggerFactory.getLogger(Step5MethodCall.class);

    private Step5MethodCall() {}

    /**
     * 解析成员中的方法调用
     */
    static void parseMethodCall(LinkedHashMap<String, TypeInfo> typeMap,
                                List<JavaParse> javaParses, BodyDeclaration<?> m,
                                TypeInfo typeInfo, MemberInfo usageInfo) {
        for (MethodCallExpr expr : m.findAll(MethodCallExpr.class)) {
            ResolvedMethodDeclaration r;
            try {
                // 代码调用次数越多，源码量越多，这里花的时间越多
                r = expr.resolve();
            } catch (Throwable e) {
                // FIXME 目前已知解析失败：静态引用，::调用
                LOG.warn("resolve fail:\n  {}.{}({}.java:1) -> {}\n  {}",
                        typeInfo.sign, usageInfo.name, typeInfo.name, expr.getNameAsString(),
                        e.getLocalizedMessage());
                continue;
            }
            ResolvedReferenceTypeDeclaration rt = r.declaringType();
            String callTypeSign = InfoUtils.resolvedTypeSign(rt);
            TypeInfo callTypeInfo = typeMap.computeIfAbsent(callTypeSign, s -> {
                TypeInfo info = new TypeInfo();
                InfoUtils.addResolvedTypeInfo(info, rt);
                return info;
            });
            MemberInfo callInfo = InfoFactory.getOrCreateMethodInfo(callTypeInfo, r);

            usageInfo.callInfo.put(callInfo.sign, callInfo);
            usageInfo.typeInfo.callInfo.put(callInfo.typeInfo.sign, callInfo.typeInfo);
            callInfo.usageInfo.put(usageInfo.sign, usageInfo);
            if (!callInfo.typeInfo.sign.equals(usageInfo.typeInfo.sign)) {
                callInfo.typeInfo.usageInfo.put(usageInfo.typeInfo.sign, usageInfo.typeInfo);
            }

            javaParses.forEach(v -> v.call(usageInfo, callInfo));
        }
    }

}
