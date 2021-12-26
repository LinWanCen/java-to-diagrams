package io.github.linwancen.util.graphviz;

import guru.nidi.graphviz.attribute.Arrow;
import guru.nidi.graphviz.attribute.Attributes;
import guru.nidi.graphviz.attribute.ForLink;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;

import static guru.nidi.graphviz.model.Factory.to;

/**
 * UML 关系
 * <a href="https://plantuml.com/zh/class-diagram">
 * https://plantuml.com/zh/class-diagram</a>
 */
public class Uml {

    private final Node node;

    private Uml(Node node) {this.node = node;}

    /** 源节点 */
    public static Uml from(Node node) {
        return new Uml(node);
    }

    /**
     * 空端点
     * https://github.com/nidi3/graphviz-java/issues/166
     */
    static final Attributes<ForLink> EMPTY_ARROW = Arrow.EMPTY.tail().dir(Arrow.DirType.BACK);

    /**
     * 1.继承
     * <br>子类 --|> 父类
     * <br>child --|> parent
     */
    public Node extend(Node parent) {
        return parent.link(to(node).with(EMPTY_ARROW));
    }

    /**
     * 2.实现
     * <br>实现 ..|> 接口
     */
    public Node implementsFor(Node interfaceNode) {
        return interfaceNode.link(to(node).with(Style.DASHED, EMPTY_ARROW));
    }

    /** 虽然是默认格式，但有时候会箭头消失所以指定好方向 */
    static final Attributes<ForLink> VEE_FORWARD = Arrow.VEE.dir(Arrow.DirType.FORWARD);

    /**
     * 3.依赖
     * <br>使用方 ..> n被用方
     * <br>usage ..> call
     */
    public Node dependency(Node call) {
        return node.link(to(call).with(Style.DASHED, VEE_FORWARD));
    }

    /**
     * 4.1.关联
     * <br>owner --> be_owned
     * <br>主人 --> 被拥有
     */
    public Node association(Node beOwned) {
        return node.link(to(beOwned).with(VEE_FORWARD));
    }

    /**
     * 4.2.双向关联
     * <br>A -- B
     */
    public Graph associationBoth(Node b) {
        return GraphvizUtils.same().with(node.link(to(b).with(Arrow.NONE)));
    }

    static final Attributes<ForLink> VEE_BOTH = Arrow.VEE.dir(Arrow.DirType.BOTH);
    static final Arrow COMPOSITION_ARROW = Arrow.DIAMOND.tail();
    static final Arrow AGGREGATION_ARROW = COMPOSITION_ARROW.open();

    /**
     * 5.聚合
     * <br>成员 <--o 整体
     * <br>members <--o overall
     */
    public Node aggregation(Node overall) {
        return overall.link(to(node).with(VEE_BOTH, AGGREGATION_ARROW));
    }

    /**
     * 组合
     * <br>部分 <--* 总体
     * <br>part <--* whole
     */
    public Node composition(Node population) {
        return population.link(to(node).with(VEE_BOTH, COMPOSITION_ARROW));
    }
}

/*
使用 plantUML 插件可以查看 UML 关系示例

@startuml
hide empty circle
hide empty members
skinparam shadowing false

parent\n父类 <|-- child\n子类 : extend\n1.继承

interface\n接口 <|.. achieve\n实现 : implements\n2.实现

usage\n使用方 ..> call\n被用方 : dependency\n3.依赖

owner\n主人 --> be_owned\n被拥有 : association\n4.1.关联

A - B : association both\n4.2.双向关联

overall\n整体 o--> members\n成员 : aggregation\n5.聚合

whole\n总体 *--> part\n部分 : composition\n6.组合
@enduml
*/