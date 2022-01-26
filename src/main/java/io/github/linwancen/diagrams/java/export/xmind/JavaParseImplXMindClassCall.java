package io.github.linwancen.diagrams.java.export.xmind;

import io.github.linwancen.diagrams.Conf;
import io.github.linwancen.diagrams.java.api.bean.MemberInfo;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;
import io.github.linwancen.util.xmind.XMindUtils;
import org.xmind.core.ITopic;
import org.xmind.core.style.IStyle;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * XMind 思维导图/脑图 类调用树 实现
 */
public class JavaParseImplXMindClassCall extends AbsJavaParseImplXMind {

    public JavaParseImplXMindClassCall(File outDir, String outName) {
        super(outDir, outName + "_class_call", "类调用树");
    }

    /**
     * 空实现
     */
    @Override
    public void type(TypeInfo typeInfo) {
    }

    /**
     * 空实现
     */
    @Override
    public void member(MemberInfo info) {
    }

    private ITopic topicFrom(TypeInfo info) {
        ITopic iTopic = workbook.createTopic();
        StringBuilder text = new StringBuilder();
        text.append(info.getCommentNotNull(0));
        text.append("\n");
        if (showSymbol) {
            text.append(info.type.symbol).append(info.modSymbol());
        }
        text.append(info.name);
        iTopic.setTitleText(text.toString());
        StringBuilder content = new StringBuilder();
        // 完整注释
        if (info.comment != null && info.comment.length() != 0) {
            content.append("\n\n").append(info.comment);
        }
        // 作者
        if (!info.authorList.isEmpty()) {
            content.append(info.getAuthor()).append("\n\n");
        }
        // 全限定名
        content.append("\n\n").append(info.sign);
        // 行数
        content.append("\n\n").append(info.lineCount);
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

    @Override
    public void typeMap(LinkedHashMap<String, TypeInfo> typeMap) {
        primarySheet.setTitleText("call");
        ITopic rootTopic = primarySheet.getRootTopic();
        rootTopic.setStructureClass(XMindUtils.LOGIC_RIGHT);
        for (Entry<String, TypeInfo> entry : typeMap.entrySet()) {
            TypeInfo typeInfo = entry.getValue();
            if (!typeInfo.usageInfo.isEmpty()
                    || !typeInfo.faceInfo.isEmpty()
                    || typeInfo.parentInfo != null
                    || typeInfo.name == null
            ) {
                continue;
            }
            ITopic topic = topicFrom(typeInfo);
            rootTopic.add(topic);
            HashMap<String, ITopic> has = new HashMap<>();
            has.put(typeInfo.sign, topic);
            rel(has, topic, typeInfo);
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
    private void rel(Map<String, ITopic> has, ITopic usageTopic, TypeInfo usage) {
        for (TypeInfo impl : usage.implInfo.values()) {
            add(has, usageTopic, usage, impl);
        }
        for (TypeInfo child : usage.childInfo.values()) {
            add(has, usageTopic, usage, child);
        }
        for (TypeInfo call : usage.callInfo.values()) {
            add(has, usageTopic, usage, call);
        }
    }

    /**
     * 添加子节点
     */
    private void add(Map<String, ITopic> has, ITopic usageTopic, TypeInfo usage, TypeInfo call) {
        // 内部调用
        if (usage.sign.equals(call.sign)) {
            return;
        }
        // 若是循环调用
        ITopic hasTopic = has.get(call.sign);
        if (hasTopic != null) {
            XMindUtils.addRel(workbook, primarySheet, usageTopic, hasTopic);
            XMindUtils.addLink(usageTopic, hasTopic);
            return;
        }
        ITopic callTopic = topicFrom(call);
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
