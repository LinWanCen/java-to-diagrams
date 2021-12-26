package io.github.linwancen.diagrams.java.parser;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import io.github.linwancen.diagrams.java.api.bean.MemberInfo;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;

/**
 * JavaInfo 工厂类
 */
class InfoFactory {

    private InfoFactory() {}

    /**
     * 获取或新建方法
     */
    static MemberInfo getOrCreateMemberInfo(TypeInfo typeInfo, ResolvedMethodDeclaration r) {
        ResolvedReferenceTypeDeclaration rt = r.declaringType();
        String sign = InfoUtils.methodSign(r);

        MemberInfo info = typeInfo.memberInfo.get(sign);
        if (info != null) {
            return info;
        }

        info = new MemberInfo();
        // 获取或新建的
        info.typeInfo = typeInfo;
        InfoUtils.addResolvedTypeInfo(info.typeInfo, rt);
        InfoUtils.addResolvedMethodInfo(info, r);
        InfoUtils.isGetSet(info, null, rt);
        typeInfo.memberInfo.put(sign, info);
        return info;
    }
}
