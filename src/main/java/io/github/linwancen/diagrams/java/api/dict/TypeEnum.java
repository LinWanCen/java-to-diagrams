package io.github.linwancen.diagrams.java.api.dict;

public enum TypeEnum {
    CLAZZ("", "class"),
    INTERFACE("I", "interface"),
    ENUM("E", "enum"),
    ANNOTATION("@", "annotation"),
    UNKNOWN("?", "unknown"),
    ;

    public final String symbol;
    public final String string;

    TypeEnum(String symbol, String string) {
        this.symbol = symbol;
        this.string = string;
    }
}
