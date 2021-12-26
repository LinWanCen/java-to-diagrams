package io.github.linwancen.diagrams.java.export.graphviz;

import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.model.Node;
import io.github.linwancen.diagrams.java.api.bean.MemberInfo;
import io.github.linwancen.util.graphviz.GraphvizUtils;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.model.Factory.node;

/**
 * 节点工具类
 */
class NodeUtils {

    private NodeUtils() {}

    /**
     * 方法块
     */
    static String[] methodRecords(MemberInfo info) {
        ArrayList<String> list = new ArrayList<>();
        String commentFirst = info.returnComment == null ? "" : info.commentFirst;
        String returnComment = info.returnComment == null || "".equals(info.returnComment)
                ? ""
                : " : " + info.returnComment;
        String comment = commentFirst + returnComment;
        if (comment.trim().length() > 0) {
            list.add(GraphvizUtils.label(GraphvizUtils.escape(comment)));
        }
        String returnType = info.returnType == null ? "" : " : " + info.returnType;
        list.add(info.modSymbol() + info.name + returnType);
        // 没有参数
        if (info.paramNames.isEmpty()) {
            return list.toArray(new String[0]);
        }
        // 有参数注释
        if (info.haveParamComments) {
            list.add(GraphvizUtils.escape(info.paramCommentsStr()));
        }
        String simpleParamNamesStr = info.simpleParamNamesStr();
        if (simpleParamNamesStr != null) {
            list.add(info.paramNamesStr());
        }
        list.add(info.paramTypes.stream()
                .map(GraphvizUtils::escape)
                .collect(Collectors.joining(", ", "(", ")")));
        return list.toArray(new String[0]);
    }

    /** 提示节点 */
    static Node tipNode(Rank.RankDir rankDir) {
        String[] recs = {
                "comment : returnComment",
                "name : returnType",
                "paramComments",
                "paramNames",
                "paramTypes",
        };
        return GraphvizUtils.rankDirRecords(node("tip"), rankDir, recs);
    }
}