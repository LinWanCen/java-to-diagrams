package io.github.linwancen.diagrams.java.parser;

import java.io.File;
import java.io.FileFilter;

@SuppressWarnings("all")
public class TestClass {

    static {
        new TestClass();
    }

    {
        new TestClass();
    }

    private String testField;

    /**
     * @return 返回值注释测试
     */
    public String getTestField() {
        return testField;
    }

    public void setTestField(String testField) {
        this.testField = testField;
    }

    private boolean testBoolean;

    /**
     * @return
     */
    public boolean isTestBoolean() {
        return testBoolean;
    }

    static class testInnerClass {
        public void name() {
            new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return false;
                }
            };
        }
    }
}
