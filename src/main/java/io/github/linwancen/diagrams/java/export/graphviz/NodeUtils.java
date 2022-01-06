package io.github.linwancen.diagrams.java.export.graphviz;

import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.model.Node;
import io.github.linwancen.diagrams.java.api.bean.MemberInfo;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;
import io.github.linwancen.util.graphviz.GraphvizUtils;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.model.Factory.node;

/**
 * 节点工具类
 */
class NodeUtils {

    private NodeUtils() {}

    /** 类块 */
    static String[] typeRecords(TypeInfo info) {
        ArrayList<String> list = new ArrayList<>();
        // 文档注释
        String comment = info.getCommentNotNull(0);
        if (comment.trim().length() > 0) {
            list.add(GraphvizUtils.label(GraphvizUtils.escape(comment)));
        }
        // 修饰符 名字 返回类型
        list.add(info.type.symbol + info.modSymbol() + info.name);
        return list.toArray(new String[0]);
    }

    /** 方法块 */
    static String[] methodRecords(MemberInfo info) {
        ArrayList<String> list = new ArrayList<>();
        // 文档注释
        String returnComment = info.returnComment == null || "".equals(info.returnComment)
                ? ""
                : " : " + info.returnComment;
        String comment = info.getCommentNotNull(0) + returnComment;
        if (comment.trim().length() > 0) {
            list.add(GraphvizUtils.label(GraphvizUtils.escape(comment)));
        }
        // 修饰符 名字 返回类型
        String returnType = info.returnType == null ? "" : " : " + GraphvizUtils.escape(info.returnType);
        list.add(info.typeInfo.type.symbol + info.modSymbol() + info.name + returnType);
        // 没有参数
        if (info.paramNames.isEmpty()) {
            return list.toArray(new String[0]);
        }
        // 有参数注释
        if (info.haveParamComments) {
            list.add(GraphvizUtils.escape(info.paramCommentsStr()));
        }
        // 参数名为类型首字母小写时省略为 ~
        String simpleParamNamesStr = info.simpleParamNamesStr();
        if (simpleParamNamesStr != null) {
            list.add(simpleParamNamesStr);
        }
        // 参数类型
        list.add(info.paramTypes.stream()
                .map(GraphvizUtils::escape)
                .collect(Collectors.joining(", ", "(", ")")));
        return list.toArray(new String[0]);
    }

    /** 提示节点 */
    static Node methodTipNode(Rank.RankDir rankDir) {
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
