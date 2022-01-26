package io.github.linwancen.diagrams.java.export.xmind;

import io.github.linwancen.diagrams.java.api.bean.MemberInfo;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;
import io.github.linwancen.diagrams.java.api.dict.MemberEnum;
import io.github.linwancen.util.xmind.XMindUtils;
import org.xmind.core.ITopic;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * XMind 思维导图/脑图 成员树 实现
 */
public class JavaParseImplXMindTree extends AbsJavaParseImplXMind {

    public JavaParseImplXMindTree(File outDir, String outName) {
        super(outDir, outName + "_tree", "成员树");
    }

    private final LinkedHashMap<String, ITopic> packageMap = new LinkedHashMap<>();
    /**
     * 类与成员节点 Map
     * <br>用于构造结构树
     */
    private final LinkedHashMap<String, LinkedHashMap<MemberEnum, LinkedHashMap<String, ITopic>>>
            typeMemberMap = new LinkedHashMap<>();
    /**
     * 成员节点 Map
     * <br>因为是一个类一个类解析的所以不在 {@link #member 中另外判断是不是属于这个类提高性能
     */
    private LinkedHashMap<MemberEnum, LinkedHashMap<String, ITopic>> currMemberMap;

    /**
     * 类处理
     */
    @Override
    public void type(TypeInfo typeInfo) {
        ITopic lastTopic = null;
        List<String> packs = typeInfo.packNames;
        for (int j = 0; j < packs.size(); j++) {
            String packName = packs.get(j);
            String packComment = typeInfo.packComments.get(j);
            ITopic iTopic = packageMap.get(packName);
            if (iTopic != null) {
                if (lastTopic != null) {
                    iTopic.add(lastTopic);
                }
                break;
            }
            iTopic = workbook.createTopic();
            int i = packName.lastIndexOf(".");
            if (i == -1) {
                rootTopic.add(iTopic);
            }
            String text = packName.substring(i + 1);
            if (packComment != null) {
                text = packComment + "\n" + text;
            }
            iTopic.setTitleText(text);
            packageMap.put(packName, iTopic);
            if (lastTopic != null) {
                iTopic.add(lastTopic);
            }
            lastTopic = iTopic;
        }
        ITopic typeTopic = workbook.createTopic();

        StringBuilder text = new StringBuilder();
        text.append(typeInfo.getCommentNotNull(0));
        text.append("\n");
        if (showSymbol) {
            text.append(typeInfo.type.symbol).append(typeInfo.modSymbol());
        }
        text.append(typeInfo.name);

        typeTopic.setTitleText(text.toString());

        StringBuilder content = new StringBuilder();
        // 完整注释
        if (typeInfo.comment != null && typeInfo.comment.length() != 0) {
            content.append(typeInfo.comment).append("\n\n");
        }
        // 作者
        if (!typeInfo.authorList.isEmpty()) {
            content.append(typeInfo.getAuthor()).append("\n\n");
        }
        // 全限定名
        content.append(typeInfo.sign).append("\n\n");
        // 行数
        content.append("\n\n").append(typeInfo.lineCount);
        XMindUtils.setNote(workbook, typeTopic, content.toString());

        typeTopic.setFolded(true);
        if (!packs.isEmpty()) {
            packageMap.get(packs.get(0)).add(typeTopic);
        } else {
            rootTopic.add(typeTopic);
        }
        packageMap.put(typeInfo.sign, typeTopic);

        currMemberMap = new LinkedHashMap<>();
        typeMemberMap.put(typeInfo.sign, currMemberMap);
    }

    /**
     * 方法生成主题
     */
    @Override
    public void member(MemberInfo info) {
        // 暂时跳过这些无关紧要的
        if (info.memberType == MemberEnum.GET_SET) {
            return;
        }
        ITopic memberTopic = workbook.createTopic();

        StringBuilder memberText = new StringBuilder();
        memberText.append(info.getCommentNotNull(0));
        memberText.append("\n");
        if (showSymbol) {
            memberText.append(info.typeInfo.type.symbol).append(info.modSymbol());
        }
        memberText.append(info.name);

        if (info.memberType == MemberEnum.CONSTRUCTOR || info.memberType == MemberEnum.METHOD) {
            memberText.append("()");
        }
        memberTopic.setTitleText(memberText.toString());

        StringBuilder content = new StringBuilder();
        // 完整注释
        if (info.comment != null && info.comment.length() != 0) {
            content.append(info.comment).append("\n\n");
        }
        // 全限定名
        content.append("\n\n").append(info.sign);
        // 行数
        content.append("\n\n").append(info.lineCount);
        XMindUtils.setNote(workbook, memberTopic, content.toString());

        LinkedHashMap<String, ITopic> map =
                currMemberMap.computeIfAbsent(info.memberType, k -> new LinkedHashMap<>());
        map.put(info.sign, memberTopic);
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

    /**
     * 空实现
     */
    @Override
    public void typeMap(LinkedHashMap<String, TypeInfo> typeMap) {

    }

    @Override
    protected void beforeSave() {
        primarySheet.setTitleText("member");
        rootTopic.setStructureClass(XMindUtils.LOGIC_RIGHT);
        typeMemberMap.forEach((type, memberMap) -> {
            ITopic typeTopic = packageMap.get(type);
            memberMap.forEach((memberEnum, topicMap) -> {
                ITopic memberTopic = workbook.createTopic();
                memberTopic.setTitleText(memberEnum.desc + "\n" + memberEnum.string);
                memberTopic.setFolded(true);
                typeTopic.add(memberTopic);
                topicMap.forEach((s, iTopic) -> memberTopic.add(iTopic));
            });
        });
    }
}
