package io.github.linwancen.diagrams.java.export.xmind;

import io.github.linwancen.diagrams.Conf;
import io.github.linwancen.diagrams.java.api.bean.MemberInfo;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;
import io.github.linwancen.diagrams.java.api.dict.MemberEnum;
import io.github.linwancen.util.java.FilterUtils;
import io.github.linwancen.util.xmind.XMindUtils;
import org.xmind.core.ITopic;
import org.xmind.core.style.IStyle;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * XMind 思维导图/脑图 调用树 实现
 */
public class JavaParseImplXMindCall extends AbsJavaParseImplXMind {

    public JavaParseImplXMindCall(File outDir, String outName) {
        super(outDir, outName + "_call", "调用树");
    }

    /**
     * 空实现
     */
    @Override
    public void type(TypeInfo typeInfo) {
    }

    private final boolean showClass = "true".equals(Conf.DIAGRAMS_XMIND_CLASS.get());

    /**
     * 空实现
     */
    @Override
    public void member(MemberInfo info) {
    }

    private final Pattern includeMethod = Pattern.compile(Conf.DIAGRAMS_XMIND_METHOD_INCLUDE.get());
    private final Pattern excludeMethod = Pattern.compile(Conf.DIAGRAMS_XMIND_METHOD_EXCLUDE.get());

    private ITopic topicFrom(MemberInfo info, boolean sameClassCall) {
        // 暂时跳过这些无关紧要的
        if (info.memberType == MemberEnum.STATIC || info.memberType == MemberEnum.FIELD || info.memberType == MemberEnum.GET_SET) {
            return null;
        }
        if (!FilterUtils.filter(info.sign, includeMethod, excludeMethod)) {
            return null;
        }
        ITopic iTopic = workbook.createTopic();
        StringBuilder text = new StringBuilder();
        text.append(info.getCommentNotNull(0));
        text.append("\n");
        if (showSymbol) {
            text.append(info.typeInfo.type.symbol).append(info.modSymbol());
        }
        if (showClass && !sameClassCall) {
            text.append(info.className()).append(".");
        }
        text.append(info.name);
        if (info.memberType == MemberEnum.CONSTRUCTOR || info.memberType == MemberEnum.METHOD) {
            text.append("()");
        }
        iTopic.setTitleText(text.toString());
        StringBuilder content = new StringBuilder();
        // 类注释
        if (info.typeInfo.comment != null) {
            content.append(info.typeInfo.comment).append("\n\n");
        }
        // 作者
        if (!info.typeInfo.authorList.isEmpty()) {
            content.append(info.typeInfo.getAuthor()).append("\n\n");
        }
        // 类名
        content.append(info.className());
        // 完整注释
        if (info.comment != null && info.comment.length() != 0) {
            content.append("\n\n").append(info.comment);
        }
        // 全限定名
        content.append("\n\n").append(info.sign);
        // 行数
        content.append("\n\n").append(info.lineCount);
        // 特殊参数名
        String simpleParamNamesStr = info.simpleParamNamesStr();
        if (simpleParamNamesStr != null) {
            content.append("\n\n").append(simpleParamNamesStr);
        }
        XMindUtils.setNote(workbook, iTopic, content.toString());
        return iTopic;
    }

    /**
     * 空实现
     */
    @Override
    public void call(MemberInfo usage, MemberInfo call) {
    }

    /**
     * 空实现
     */
    @Override
    public void extend(MemberInfo child, MemberInfo parent) {
    }

    /**
     * 空实现
     */
    @Override
    public void impl(MemberInfo impl, MemberInfo face) {
    }

    private final Pattern includeRoot = Pattern.compile(Conf.DIAGRAMS_XMIND_INCLUDE.get());
    private final Pattern excludeRoot = Pattern.compile(Conf.DIAGRAMS_XMIND_EXCLUDE.get());

    @Override
    public void typeMap(LinkedHashMap<String, TypeInfo> typeMap) {
        primarySheet.setTitleText("call");
        ITopic rootTopic = primarySheet.getRootTopic();
        rootTopic.setStructureClass(XMindUtils.LOGIC_RIGHT);

        for (TypeInfo typeInfo : typeMap.values()) {
            for (MemberInfo info : typeInfo.memberInfo.values()) {
                if (!FilterUtils.filter(info.sign, includeRoot, excludeRoot)) {
                    continue;
                }
                if (!info.usageInfo.isEmpty() || !info.faceInfo.isEmpty() || info.parentInfo != null) {
                    continue;
                }
                ITopic topic = topicFrom(info, false);
                // 调用方被省略（字段、静态）
                if (topic == null) {
                    continue;
                }
                rootTopic.add(topic);
                HashMap<String, ITopic> has = new HashMap<>();
                has.put(info.sign, topic);
                rel(has, topic, info);
            }
        }

        // 最后再判断独立节点是因为可能前面被筛掉导致独立
        alone(rootTopic, workbook.createTopic());
    }

    /**
     * 空实现
     */
    @Override
    protected void beforeSave() {
    }

    private final boolean hideCallOther = "true".equals(Conf.DIAGRAMS_XMIND_HIDE_CALL_OTHER.get());

    /**
     * 调用实现继承关系添加子节点
     */
    private void rel(Map<String, ITopic> has, ITopic usageTopic, MemberInfo usage) {
        for (MemberInfo impl : usage.implInfo.values()) {
            add(has, usageTopic, usage, impl);
        }
        for (MemberInfo child : usage.childInfo.values()) {
            add(has, usageTopic, usage, child);
        }
        for (MemberInfo call : usage.callInfo.values()) {
            add(has, usageTopic, usage, call);
        }
    }

    /**
     * 添加子节点
     */
    private void add(Map<String, ITopic> has, ITopic usageTopic, MemberInfo usage, MemberInfo call) {
        // 若是递归调用
        if (usage.sign.equals(call.sign)) {
            usageTopic.setTitleText(usageTopic.getTitleText() + "↺");
            return;
        }
        // 若是循环调用
        ITopic hasTopic = has.get(call.sign);
        if (hasTopic != null) {
            XMindUtils.addRel(workbook, primarySheet, usageTopic, hasTopic);
            XMindUtils.addLink(usageTopic, hasTopic);
            return;
        }
        boolean sameClassCall = usage.typeInfo.sign.equals(call.typeInfo.sign);
        ITopic callTopic = topicFrom(call, sameClassCall);
        // 被筛选掉的方法跳过
        if (callTopic == null) {
            return;
        }
        if (!call.isSelect) {
            if (hideCallOther) {
                return;
            }
            // 非选择的设置黄色底色
            XMindUtils.addStyle(workbook, IStyle.TOPIC, callTopic, XMindUtils.FILL_COLOR, "#FDD834");
        }
        if (call.usageInfo.size() > 1) {
            // 被多个调用时设置红色字体
            XMindUtils.addStyle(workbook, IStyle.TOPIC, callTopic, XMindUtils.FONT_COLOR, "#FF3C00");
        }
        usageTopic.add(callTopic);
        // 避免循环调用
        has = new HashMap<>(has);
        has.put(call.sign, callTopic);
        // 递归调用
        rel(has, callTopic, call);
    }

}
