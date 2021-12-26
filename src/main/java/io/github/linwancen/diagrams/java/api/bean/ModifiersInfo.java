package io.github.linwancen.diagrams.java.api.bean;

import io.github.linwancen.diagrams.java.api.dict.AccessEnum;
import io.github.linwancen.diagrams.java.api.dict.MemberEnum;
import io.github.linwancen.diagrams.java.api.dict.TypeEnum;

/**
 * 常用修饰词
 * <br>按谷歌顺序排列
 * <br>public protected private abstract default static final transient volatile synchronized native strictfp
 */
public class ModifiersInfo {
    /** 可见性 */
    public AccessEnum access;

    /** 抽象 */
    public boolean isAbstract;

    /** 抽象 修饰词 */
    public String abstractStr() {
        return isAbstract ? "abstract" : "";
    }

    /** 抽象 修饰符 */
    public String abstractSymbol() {
        return isAbstract ? "a" : "";
    }

    /** 静态 */
    public boolean isStatic;

    /** 静态 修饰词 */
    public String staticStr() {
        return isStatic ? "static" : "";
    }

    /** 静态 修饰符 */
    public String staticSymbol() {
        return isStatic ? "s" : "";
    }

    /** 终态 */
    public boolean isFinal;

    /** 终态 修饰词 */
    public String finalStr() {
        return isFinal ? "final" : "";
    }

    /** 终态 修饰符 */
    public String finalSymbol() {
        return isFinal ? "f" : "";
    }

    /** 类型 */
    public TypeEnum type;

    /** 成员类型 */
    public MemberEnum memberType;

    /**
     * 返回所有修饰符
     * <br>最后加空格以便拼接
     */
    public String modSymbol() {
        String s = abstractSymbol() + staticSymbol() + finalSymbol() + access.symbol;
        return type == null ? s : s + type.symbol + " ";
    }

    /**
     * 返回所有修饰词
     * <br>最后加空格以便拼接
     */
    public String modStr() {
        String s = access.string + " " + abstractStr() + " " + staticStr() + " " + finalStr() + " ";
        return type == null ? s : s + type.string + " ";
    }
}
