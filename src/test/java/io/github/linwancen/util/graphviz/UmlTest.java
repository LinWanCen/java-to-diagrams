package io.github.linwancen.util.graphviz;

import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.model.Graph;
import org.testng.annotations.Test;

import java.io.File;

import static guru.nidi.graphviz.model.Factory.node;

public class UmlTest {

    @Test
    public void test() {
        Graph g = GraphvizUtils.initWithFont(Rank.RankDir.LEFT_TO_RIGHT, "");
        g = g.with(Uml.from(node("child\n子类")).extend(node("parent\n父类")));
        g = g.with(Uml.from(node("achieve\n实现")).implementsFor(node("interface\n接口")));
        g = g.with(Uml.from(node("usage\n使用方")).dependency(node("call\n被用方")));
        g = g.with(Uml.from(node("owner\n主人")).association(node("be_owned\n被拥有")));
        g = g.with(Uml.from(node("A")).associationBoth(node("B")));
        g = g.with(Uml.from(node("members\n成员")).aggregation(node("overall\n整体")));
        g = g.with(Uml.from(node("part\n部分")).composition(node("whole\n总体")));
        @SuppressWarnings("ConstantConditions")
        File dir = new File(this.getClass().getResource("").getPath());
        GraphvizUtils.toFile(g, dir, "UmlTest", "UML 示意图");
    }
}