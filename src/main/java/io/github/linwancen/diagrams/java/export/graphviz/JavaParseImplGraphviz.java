package io.github.linwancen.diagrams.java.export.graphviz;

import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import io.github.linwancen.diagrams.Conf;
import io.github.linwancen.diagrams.java.api.JavaParse;
import io.github.linwancen.diagrams.java.api.bean.MemberInfo;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;
import io.github.linwancen.diagrams.java.api.dict.MemberEnum;
import io.github.linwancen.util.graphviz.GraphvizUtils;
import io.github.linwancen.util.graphviz.Uml;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

/**
 * Graphviz 方法关系图 实现
 */
public class JavaParseImplGraphviz implements JavaParse {

    private final LinkedHashMap<String, LinkedHashMap<String, Node>> typeMethodMap = new LinkedHashMap<>();

    private final Rank.RankDir rankDir = Rank.RankDir.valueOf(Conf.DIAGRAMS_DOT_DIR.get());

    private Graph g = GraphvizUtils.initWithFont(rankDir, "");

    public final File outDir;
    public final String outName;

    public JavaParseImplGraphviz(File outDir, String outName) {
        this.outDir = outDir;
        this.outName = outName;
    }

    /**
     * 类边框
     * <br>因为是一个类一个类解析的所以不在 method 中另外判断是不是属于这个类提高性能
     */
    private Graph cluster;

    /**
     * 类处理
     */
    @Override
    public void type(TypeInfo typeInfo) {
        if (cluster != null) {
            g = g.with(cluster);
        }
        String text = typeInfo.getCommentLine(0) + typeInfo.type.symbol + typeInfo.modSymbol() + typeInfo.sign;
        text = GraphvizUtils.escape(text);
        cluster = graph(typeInfo.sign + "_C")
                .cluster()
                .graphAttr()
                .with(Color.BLUE, Label.of(text));
        typeMethodMap.put(typeInfo.sign, new LinkedHashMap<>());
    }

    private final boolean showField = "true".equals(Conf.DIAGRAMS_DOT_FIELD.get());

    /**
     * 方法生成主题
     */
    @Override
    public void member(MemberInfo info) {
        if (info.memberType == MemberEnum.GET_SET) {
            return;
        }
        Node node = node(GraphvizUtils.escape(info.sign));
        // 字段下划线
        if (info.memberType == MemberEnum.FIELD && !showField) {
            return;
        }
        // 构造方法
        if (info.memberType == MemberEnum.CONSTRUCTOR) {
            node = node.with(Style.BOLD);
        }
        node = GraphvizUtils.rankDirRecords(node, this.rankDir, NodeUtils.methodRecords(info));
        // 字段下划线
        if (info.memberType == MemberEnum.FIELD) {
            node = node.with(Shape.UNDERLINE);
        }
        cluster = cluster.with(node);
        typeMethodMap.get(info.typeInfo.sign).put(info.sign, node);
    }


    private final List<MemberInfo[]> usageCallList = new ArrayList<>();

    /**
     * 保存调用关系
     */
    @Override
    public void call(MemberInfo usage, MemberInfo call) {
        usageCallList.add(new MemberInfo[]{usage, call});
    }

    private final List<MemberInfo[]> extendList = new ArrayList<>();

    /**
     * 保存继承关系
     */
    @Override
    public void extend(MemberInfo child, MemberInfo parent) {
        extendList.add(new MemberInfo[]{child, parent});
    }

    private final List<MemberInfo[]> implList = new ArrayList<>();

    /**
     * 保存实现关系
     */
    @Override
    public void impl(MemberInfo impl, MemberInfo face) {
        implList.add(new MemberInfo[]{impl, face});
    }

    /**
     * 空实现
     */
    @Override
    public void typeMap(LinkedHashMap<String, TypeInfo> typeMap) {
    }

    /**
     * 处理并生成调用关系图
     */
    @Override
    public void end() {
        // 包含最后一个类
        if (cluster != null) {
            g = g.with(cluster);
        }
        forCall();
        CallUtils.forOver(extendList, typeMethodMap,
                (parent, over) -> g = g.with(Uml.from(over).extend(parent)));
        CallUtils.forOver(implList, typeMethodMap,
                (parent, over) -> g = g.with(Uml.from(over).implementsFor(parent)));
        g = g.with(graph("tip").cluster()
                .graphAttr().with(Color.BLUE, Label.of("tip:class"))
                .with(NodeUtils.methodTipNode(rankDir))
                .with(node("tip_field").with(Records.of(" 字段\nfield")).with(Shape.UNDERLINE))
                .with(node("tip_constructor").with(Records.of(" 构造方法\nconstructor")).with(Style.BOLD))
        );
        GraphvizUtils.toFile(g, outDir, outName, "方法关系图");
    }

    /**
     * 方法调用
     */
    private void forCall() {
        for (MemberInfo[] infos : usageCallList) {
            MemberInfo usageInfo = infos[0];
            MemberInfo callInfo = infos[1];
            LinkedHashMap<String, Node> callMethodMap = typeMethodMap.get(callInfo.typeInfo.sign);
            // 如果调用的方法不在扫描的类中则跳过
            if (callMethodMap == null) {
                continue;
            }
            Node call = callMethodMap.get(callInfo.sign);
            // 被筛选掉的方法跳过
            if (call == null) {
                continue;
            }
            Node usage = typeMethodMap.get(usageInfo.typeInfo.sign).get(usageInfo.sign);
            // 调用关系不能写在 cluster 里，否则会把被调的节点也放进去
            g = g.with(Uml.from(usage).dependency(call));
        }
    }
}
