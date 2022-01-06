package io.github.linwancen.diagrams.java.api.bean;

import io.github.linwancen.diagrams.java.api.dict.TypeEnum;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author 林万程
 * @author linwancheng
 */
public class TypeInfo extends JavaInfo {
    /** 类型 */
    public TypeEnum type;

    /** 作者 */
    public List<String> authorList = new ArrayList<>();

    public String getAuthor() {
        return String.join("\n", authorList);
    }

    /** 成员 */
    public LinkedHashMap<String, MemberInfo> memberInfo = new LinkedHashMap<>();

    /** 被调 */
    public LinkedHashMap<String, TypeInfo> usageInfo = new LinkedHashMap<>();
    /** 调用 */
    public LinkedHashMap<String, TypeInfo> callInfo = new LinkedHashMap<>();
    /** 接口 */
    public LinkedHashMap<String, TypeInfo> faceInfo = new LinkedHashMap<>();
    /** 实现 */
    public LinkedHashMap<String, TypeInfo> implInfo = new LinkedHashMap<>();
    /** 继承父 */
    public TypeInfo parentInfo;
    /** 继承子 */
    public LinkedHashMap<String, TypeInfo> childInfo = new LinkedHashMap<>();
}
