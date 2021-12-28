package io.github.linwancen.diagrams.java.parser;

import io.github.linwancen.diagrams.JavaParserMain;
import org.testng.annotations.Test;

@SuppressWarnings("unused")
public class JavaParseMainTest {

    /**
     * 直接运行 {@link JavaParserMain#main} 可以图形化界面选择
     */
    @Test
    public void test() {
        // 解析路径 分隔符 \r 或 \n 或 Windows ; Mac/Linux :
        // 这里也可以设置多个字符串
        // IDEA 可以选中多个文件或文件夹 Ctrl + Shift + C 复制路径
        // IDEA 粘贴到这里会自动转义，如果不喜欢转义可以设置环境变量 parser_path
        JavaParserMain.main("");
    }
}