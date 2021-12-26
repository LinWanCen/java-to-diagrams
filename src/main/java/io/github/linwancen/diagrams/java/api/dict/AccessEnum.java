package io.github.linwancen.diagrams.java.api.dict;

/**
 * 可见性枚举值
 * <br>+ 公有 - 私有 # 保护 ~ 包级(默认)
 * <br>https://plantuml.com/zh/class-diagram
 */
public enum AccessEnum {
    PUBLIC("+", "public"),
    PRIVATE("-", "private"),
    PROTECTED("#", "protected"),
    PACKAGE_PRIVATE("~", ""),
    UNKNOWN("?", "unknown"),
    NONE(" ", ""),
    ;

    public final String symbol;
    public final String string;

    AccessEnum(String symbol, String string) {
        this.symbol = symbol;
        this.string = string;
    }
}
