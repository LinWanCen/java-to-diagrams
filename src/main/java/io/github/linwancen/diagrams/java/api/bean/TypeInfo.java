package io.github.linwancen.diagrams.java.api.bean;

import java.util.LinkedHashMap;

public class TypeInfo extends JavaInfo {
    /** 成员 */
    public LinkedHashMap<String, MemberInfo> memberInfo = new LinkedHashMap<>();

    /** 被调 */
    public LinkedHashMap<String, JavaInfo> usageInfo = new LinkedHashMap<>();
    /** 调用 */
    public LinkedHashMap<String, JavaInfo> callInfo = new LinkedHashMap<>();
    /** 接口 */
    public LinkedHashMap<String, JavaInfo> faceInfo = new LinkedHashMap<>();
    /** 实现 */
    public LinkedHashMap<String, JavaInfo> implInfo = new LinkedHashMap<>();
    /** 继承父 */
    public JavaInfo parentInfo;
    /** 继承子 */
    public LinkedHashMap<String, JavaInfo> childInfo = new LinkedHashMap<>();
}
