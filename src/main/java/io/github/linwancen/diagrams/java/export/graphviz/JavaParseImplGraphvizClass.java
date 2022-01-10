package io.github.linwancen.diagrams.java.export.graphviz;

import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import io.github.linwancen.diagrams.Conf;
import io.github.linwancen.diagrams.java.api.JavaParse;
import io.github.linwancen.diagrams.java.api.bean.MemberInfo;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;
import io.github.linwancen.util.graphviz.GraphvizUtils;
import io.github.linwancen.util.graphviz.Uml;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static guru.nidi.graphviz.model.Factory.node;

/**
 * Graphviz 类关系图 实现
 */
public class JavaParseImplGraphvizClass implements JavaParse {

    private final LinkedHashMap<String, Node> nodeMap = new LinkedHashMap<>();

    private final Rank.RankDir rankDir = Rank.RankDir.valueOf(Conf.DIAGRAMS_DOT_DIR.get());

    private Graph g = GraphvizUtils.initWithFont(rankDir, "");

    public final File outDir;
    public final String outName;

    public JavaParseImplGraphvizClass(File outDir, String outName) {
        this.outDir = outDir;
        this.outName = outName + "_class";
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

    LinkedHashMap<String, TypeInfo> typeMap;

    @Override
    public void typeMap(LinkedHashMap<String, TypeInfo> typeMap) {
        this.typeMap = typeMap;
        for (Map.Entry<String, TypeInfo> entry : typeMap.entrySet()) {
            TypeInfo typeInfo = entry.getValue();
            if (!typeInfo.usageInfo.isEmpty()
                    || !typeInfo.faceInfo.isEmpty()
                    || typeInfo.parentInfo != null
                    || typeInfo.name == null
            ) {
                continue;
            }
            Node node = nodeFrom(typeInfo);
            HashMap<String, Node> has = new HashMap<>();
            has.put(typeInfo.sign, node);
            rel(has, node, typeInfo);
        }
    }

    private Node nodeFrom(TypeInfo info) {
        return nodeMap.computeIfAbsent(info.sign, s -> {
            Node node = node(GraphvizUtils.escape(info.sign));
            node = GraphvizUtils.rankDirRecords(node, this.rankDir, NodeUtils.typeRecords(info));
            g = g.with(node);
            return node;
        });
    }

    @Override
    public void end() {
        GraphvizUtils.toFile(g, outDir, outName, "类关系图");
    }

    private final BiConsumer<Node, Node> implFun = (usage, impl) -> g = g.with(Uml.from(impl).implementsFor(usage));
    private final BiConsumer<Node, Node> childFun = (usage, child) -> g = g.with(Uml.from(child).extend(usage));
    private final BiConsumer<Node, Node> callFun = (usage, call) -> g = g.with(Uml.from(usage).dependency(call));

    /**
     * 调用实现继承关系添加子节点
     */
    private void rel(Map<String, Node> has, Node usageNode, TypeInfo usage) {
        for (TypeInfo impl : usage.implInfo.values()) {
            add(has, usageNode, usage, impl, implFun);
        }
        for (TypeInfo child : usage.childInfo.values()) {
            add(has, usageNode, usage, child, childFun);
        }
        for (TypeInfo call : usage.callInfo.values()) {
            add(has, usageNode, usage, call, callFun);
        }
    }

    /**
     * 添加子节点
     */
    private void add(Map<String, Node> has, Node usageNode, TypeInfo usage, TypeInfo call,
                     BiConsumer<Node, Node> link) {
        // 内部调用
        if (usage.sign.equals(call.sign)) {
            return;
        }
        // 若是循环调用
        Node hasNode = has.get(call.sign);
        if (hasNode != null) {
            link.accept(usageNode, hasNode);
            return;
        }
        if (!call.isSelect) {
            return;
        }
        Node callNode = nodeFrom(call);
        link.accept(usageNode, callNode);
        // 避免循环调用
        has = new HashMap<>(has);
        has.put(call.sign, callNode);
        // 递归调用
        rel(has, callNode, call);
    }
}
