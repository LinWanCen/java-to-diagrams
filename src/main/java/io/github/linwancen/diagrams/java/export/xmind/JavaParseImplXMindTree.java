package io.github.linwancen.diagrams.java.export.xmind;

import io.github.linwancen.diagrams.java.api.bean.JavaInfo;
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
        super(outDir, outName + "_tree");
    }

    @Override
    protected String tipName() {
        return "成员树";
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
    private LinkedHashMap<MemberEnum, LinkedHashMap<String, ITopic>> memberMap;

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

        StringBuilder text = stringBuilderForm(typeInfo);

        typeTopic.setTitleText(text.toString());
        // 完整注释
        StringBuilder content = new StringBuilder();
        if (typeInfo.comment != null) {
            content.append(content).append("\n\n");
        }
        if (!typeInfo.author.isEmpty()) {
            content.append(String.join("\n", typeInfo.author));
        }
        if (content.length() > 0) {
            XMindUtils.setNote(workbook, typeTopic, content.toString());
        }
        typeTopic.setFolded(true);
        if (packs.size() > 0) {
            packageMap.get(packs.get(0)).add(typeTopic);
        } else {
            rootTopic.add(typeTopic);
        }
        packageMap.put(typeInfo.sign, typeTopic);

        memberMap = new LinkedHashMap<>();
        typeMemberMap.put(typeInfo.sign, memberMap);
    }

    /**
     * 注释符号和名字
     */
    private StringBuilder stringBuilderForm(JavaInfo typeInfo) {
        StringBuilder text = new StringBuilder();
        if (typeInfo.commentFirst != null) {
            text.append(typeInfo.commentFirst);
        }
        text.append("\n");
        if (showSymbol) {
            text.append(typeInfo.modSymbol());
        }
        text.append(typeInfo.name);
        return text;
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

        StringBuilder memberText = stringBuilderForm(info);

        if (info.memberType == MemberEnum.CONSTRUCTOR || info.memberType == MemberEnum.METHOD) {
            memberText.append("()");
        }
        memberTopic.setTitleText(memberText.toString());
        if (info.comment != null) {
            XMindUtils.setNote(workbook, memberTopic, info.comment);
        }
        LinkedHashMap<String, ITopic> map =
                memberMap.computeIfAbsent(info.memberType, k -> new LinkedHashMap<>());
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
