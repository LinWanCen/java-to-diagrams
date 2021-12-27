package io.github.linwancen.diagrams.java.api;

import io.github.linwancen.diagrams.java.api.bean.MemberInfo;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;

import java.util.LinkedHashMap;

/**
 * Java 解析接口
 * <br>没有r是为了类名不重复
 */
public interface JavaParse {
    /** 类处理 */
    void type(TypeInfo typeInfo);

    /** 方法处理 */
    void member(MemberInfo info);

    /** 方法调用关系处理 */
    void call(MemberInfo usage, MemberInfo call);

    /** 继承关系处理 */
    void extend(MemberInfo child, MemberInfo parent);

    /** 实现关系处理 */
    void impl(MemberInfo impl, MemberInfo face);

    /** 所有类关系处理 */
    void typeMap(LinkedHashMap<String, TypeInfo> typeMap);

    /** 结束处理 */
    void end();
}
