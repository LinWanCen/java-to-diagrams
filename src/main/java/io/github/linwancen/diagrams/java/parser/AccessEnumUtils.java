package io.github.linwancen.diagrams.java.parser;

import com.github.javaparser.ast.AccessSpecifier;
import io.github.linwancen.diagrams.java.api.dict.AccessEnum;

/**
 * 可见性工具类
 */
class AccessEnumUtils {

    private AccessEnumUtils() {}

    /**
     * 可见性转符号枚举
     * <br>+ 公有 - 私有 # 保护 ~ 包级(默认)
     */
    static AccessEnum toEnum(AccessSpecifier accessSpecifier) {
        switch (accessSpecifier) {
            case PUBLIC:
                return AccessEnum.PUBLIC;
            case PRIVATE:
                return AccessEnum.PRIVATE;
            case PROTECTED:
                return AccessEnum.PROTECTED;
            case PACKAGE_PRIVATE:
                return AccessEnum.PACKAGE_PRIVATE;
            default:
                return AccessEnum.UNKNOWN;
        }
    }
}
