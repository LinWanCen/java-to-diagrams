package io.github.linwancen.diagrams.java.api.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MemberInfo extends JavaInfo {
    private static final Logger LOG = LoggerFactory.getLogger(MemberInfo.class);

    /** 类 */
    public TypeInfo typeInfo;

    /** 非静态调用时返回小写开头的类名 */
    public String className() {
        return isStatic ? typeInfo.name : typeInfo.lowFirstName;
    }

    /** 参数类名 */
    public List<String> paramTypes = new ArrayList<>();
    /** 参数名 */
    public List<String> paramNames = new ArrayList<>();
    /** 参数注释 */
    public List<String> paramComments = new ArrayList<>();
    /** 有参数注释 */
    public boolean haveParamComments;

    public String paramTypesStr() {
        return paramTypes.stream().collect(Collectors.joining(", ", "(", ")"));
    }

    public String paramNamesStr() {
        return paramNames.stream().collect(Collectors.joining(", ", "(", ")"));
    }

    public String simpleParamNamesStr() {
        List<String> simpleNames = new ArrayList<>();
        boolean hasNameDiff = false;
        for (int i = 0; i < paramNames.size(); i++) {
            String paramName = paramNames.get(i);
            String paramType = paramTypes.get(i);
            int index = paramType.indexOf("<");
            if (index >= 0) {
                paramType = paramType.substring(0, index);
            }
            if (paramName.substring(1).equals(paramType.substring(1))) {
                simpleNames.add("~");
            } else {
                simpleNames.add(paramName);
                hasNameDiff = true;
            }
        }
        if (hasNameDiff) {
            return simpleNames.stream()
                    .collect(Collectors.joining(", ", "(", ")"));
        } else {
            return null;
        }
    }

    public String paramCommentsStr() {
        return paramComments.stream().collect(Collectors.joining(", ", "(", ")"));
    }

    /** 返回类名 */
    public String returnType;
    /** 返回注释 */
    public String returnComment;

    protected void logCommentFirst() {
        LOG.warn("commentGenFirst fail:\n{}.{}({}.java:1) \n{}", typeInfo.sign, name, typeInfo.name, comment);
    }

    /** 被调 */
    public LinkedHashMap<String, MemberInfo> usageInfo = new LinkedHashMap<>();
    /** 调用 */
    public LinkedHashMap<String, MemberInfo> callInfo = new LinkedHashMap<>();
    /** 接口 */
    public LinkedHashMap<String, MemberInfo> faceInfo = new LinkedHashMap<>();
    /** 实现 */
    public LinkedHashMap<String, MemberInfo> implInfo = new LinkedHashMap<>();
    /** 继承父 */
    public MemberInfo parentInfo;
    /** 继承子 */
    public LinkedHashMap<String, MemberInfo> childInfo = new LinkedHashMap<>();
}
