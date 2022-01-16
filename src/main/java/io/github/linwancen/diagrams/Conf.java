package io.github.linwancen.diagrams;

import io.github.linwancen.util.java.EnvUtils;

import java.util.Properties;

/**
 * 配置
 * <br>修改配置应使用 System.setProperty(Conf.DIAGRAMS_PATH.key, "");
 * <br>注意 include exclude 配置用的是正则表达式，在配置中 \ 要转义成 \\
 * <br>前缀 diagrams_ 不设置成变量拼接是为了 IDE 能智能识别
 */
public enum Conf {

    // region 解析路径

    /** 解析路径 分隔符 Windows ; Mac/Linux : */
    DIAGRAMS_PATH("diagrams_path", null),

    /** 包含文件路径正则 */
    DIAGRAMS_PATH_INCLUDE("diagrams_path_include",
            // language="regexp"
            "\\.java$"),

    /** 排除文件路径正则 */
    DIAGRAMS_PATH_EXCLUDE("diagrams_path_exclude", ""),

    // endregion 解析路径


    // region 寻找类

    /** 解析 在源码根路径中 分隔符 Windows ; Mac/Linux : */
    DIAGRAMS_SOLVER_SRC("diagrams_solver_src", ""),
    /** 解析 在 jar 中 分隔符 Windows ; Mac/Linux : */
    DIAGRAMS_SOLVER_JAR("diagrams_solver_jar", ""),
    /** 解析 自动获取 源码目录 */
    DIAGRAMS_SOLVER_AUTO_SRC("diagrams_solver_auto_src", "true"),
    /** 解析 自动获取 maven jar，需要设置Maven环境变量 */
    DIAGRAMS_SOLVER_AUTO_JAR("diagrams_solver_auto_jar", "true"),
    /** 解析 自动获取 maven jar，需要设置Maven环境变量 */
    DIAGRAMS_SOLVER_AUTO_JAR_EACH("diagrams_solver_auto_jar_each", "false"),

    // endregion 寻找类


    /** 生成文件后缀 */
    DIAGRAMS_OUT_SUFFIX("diagrams_out_suffix", "_code_diagrams"),

    /** 表格 */
    DIAGRAMS_EXCEL("diagrams_excel", "true"),

    // region 关系图

    /** 类关系图 */
    DIAGRAMS_DOT_CLASS("diagrams_dot_class", "true"),
    /** 方法关系图 */
    DIAGRAMS_DOT("diagrams_dot", "true"),
    /** 方法关系图 显示字段 */
    DIAGRAMS_DOT_FIELD("diagrams_dot_field", "false"),
    /** 关系图 方向 */
    DIAGRAMS_DOT_DIR("diagrams_dot_dir", "LEFT_TO_RIGHT"),

    // endregion 关系图


    // region 思维导图

    /** 思维导图 成员树 */
    @SuppressWarnings("SpellCheckingInspection")
    DIAGRAMS_XMIND_TREE("diagrams_xmind_tree", "true"),

    /** 思维导图 调用树 */
    @SuppressWarnings("SpellCheckingInspection")
    DIAGRAMS_XMIND_CALL("diagrams_xmind_call", "true"),

    /** 思维导图 类调用树 */
    @SuppressWarnings("SpellCheckingInspection")
    DIAGRAMS_XMIND_CLASS_CALL("diagrams_xmind_class_call", "true"),

    /** 思维导图显示类名 */
    @SuppressWarnings("SpellCheckingInspection")
    DIAGRAMS_XMIND_CLASS("diagrams_xmind_class", "true"),

    /** 思维导图显示标识符 */
    @SuppressWarnings("SpellCheckingInspection")
    DIAGRAMS_SHOW_SYMBOL("diagrams_xmind_symbol", "true"),

    /** 思维导图 根方法 包含正则 */
    @SuppressWarnings("SpellCheckingInspection")
    DIAGRAMS_XMIND_INCLUDE("diagrams_xmind_include", ""),

    /** 思维导图 根方法 排除正则 */
    @SuppressWarnings("SpellCheckingInspection")
    DIAGRAMS_XMIND_EXCLUDE("diagrams_xmind_exclude", ""),

    /** 思维导图 方法 包含正则 */
    @SuppressWarnings("SpellCheckingInspection")
    DIAGRAMS_XMIND_METHOD_INCLUDE("diagrams_xmind_method_include", ""),

    /** 思维导图 方法 排除正则 */
    @SuppressWarnings("SpellCheckingInspection")
    DIAGRAMS_XMIND_METHOD_EXCLUDE("diagrams_xmind_method_exclude",
            // language="regexp"
            "^java"),

    /** 思维导图 隐藏调用的其他方法 */
    @SuppressWarnings("SpellCheckingInspection")
    DIAGRAMS_XMIND_HIDE_CALL_OTHER("diagrams_xmind_hide_call_other", "true"),

    // endregion 思维导图
    ;

    public final String key;
    public final String defaultValue;

    Conf(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public static String confPath = "conf.properties";
    public static Properties prop;

    public String get() {
        return EnvUtils.get(key, prop, defaultValue);
    }
}
