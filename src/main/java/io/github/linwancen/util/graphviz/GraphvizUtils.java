package io.github.linwancen.util.graphviz;

import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizCmdLineEngine;
import guru.nidi.graphviz.engine.GraphvizException;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static guru.nidi.graphviz.attribute.Rank.RankType.SAME;
import static guru.nidi.graphviz.attribute.Records.turn;
import static guru.nidi.graphviz.model.Factory.*;

/**
 * Graphviz 工具类
 */
public class GraphvizUtils {

    private GraphvizUtils() {}

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphvizUtils.class);
    private static final String FONT_NAME = "Microsoft YaHei";

    /** 初始化字体 */
    public static Graph initWithFont(Rank.RankDir rankDir, String label) {
        return graph("G").directed()
                .graphAttr().with(Font.name(FONT_NAME), Rank.dir(rankDir), Label.of(label))
                .linkAttr().with(Font.name(FONT_NAME))
                .nodeAttr().with(Font.name(FONT_NAME));
    }

    /** 转义标签 */
    public static String escape(String labelName) {
        return labelName
                .replace("<", "\\<")
                .replace(">", "\\>")
                .replace("{", "\\{")
                ;
    }

    /** 标签两侧增加字符优化选择体验 */
    public static String label(String label) {
        if (label == null) {
            return "";
        }
        // 第一个中文会空白所以添加空格
        return " " + label;
    }

    /** 同级 */
    public static Graph same() {
        return graph().graphAttr().with(Rank.inSubgraph(SAME));
    }

    /** 宽度工具 */
    public static Node width(String prefix, int... widths) {
        Node curryNode = widthNode(prefix + (widths.length - 1), widths[widths.length - 1]);
        for (int i = widths.length - 2; i >= 0; i--) {
            curryNode = widthNode(prefix + i, widths[i]).link(to(curryNode).with(Style.INVIS));
        }
        return curryNode;
    }

    /** 宽度节点 */
    private static Node widthNode(String name, int width) {
        char[] withArray = new char[width];
        Arrays.fill(withArray, '-');
        return node(name).with(Records.of(new String(withArray)), Style.INVIS);
    }

    /** 按方向处理节点 */
    public static Node rankDirRecords(Node node, Rank.RankDir rankDir, String... records) {
        switch (rankDir) {
            case BOTTOM_TO_TOP:
            case TOP_TO_BOTTOM:
                node = node.with(Records.of(turn(records)));
                break;
            case LEFT_TO_RIGHT:
            case RIGHT_TO_LEFT:
                node = node.with(Records.of(records));
                break;
        }
        return node;
    }

    /** 生成文件 */
    public static void toFile(Graph g, File dir, String name, String tip) {
        try (GraphvizCmdLineEngine engine = new GraphvizCmdLineEngine()) {
            // 避免超时：Command took too long to execute, try setting a higher timout:
            Graphviz.useEngine(engine.timeout(2, TimeUnit.MINUTES));
            Graphviz graph = Graphviz.fromGraph(g);
            Format[] formats = {
                    Format.PNG,
                    Format.SVG,
            };
            File file = null;
            try {
                for (Format format : formats) {
                    file = new File(dir, name + "." + format.fileExtension);
                    graph.render(format).toFile(file);
                    String path = file.getAbsolutePath().replace('\\', '/');
                    LOGGER.info("{} {} 格式：\tfile:///{}", tip, format.fileExtension, path);
                }
            } catch (IOException e) {
                LOGGER.info(file.getAbsolutePath(), e);
            } catch (GraphvizException e) {
                LOGGER.warn("解析报错 or 没有配置环境变量 or 没有安装 Graphviz：https://www2.graphviz.org/Archive/stable/\n  {}",
                        e.getLocalizedMessage());
            }
        }
    }
}
