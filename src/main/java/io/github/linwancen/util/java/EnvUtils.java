package io.github.linwancen.util.java;

import java.util.Properties;


/**
 * 读取参数
 * <br>优先级：命令行 > 环境变量 > 配置文件 > 默认值
 * <br>命令行：-D key=value
 */
public class EnvUtils {

    private EnvUtils() {}

    /**
     * 读取参数
     * <br>优先级：命令行 > 环境变量 > 配置文件 > 默认值
     * <br>命令行：-Dkey=value
     */
    public static String get(String key, Properties prop, String defaultValue) {
        String property = System.getProperty(key);
        if (property != null) {
            return property;
        }
        String env = System.getenv(key);
        if (env != null) {
            return env;
        }
        if (prop != null) {
            String value = prop.getProperty(key);
            if (value != null) {
                return value;
            }
        }
        return defaultValue;
    }
}
