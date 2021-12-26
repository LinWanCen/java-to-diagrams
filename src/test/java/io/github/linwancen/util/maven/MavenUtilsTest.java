package io.github.linwancen.util.maven;

import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertNotNull;

public class MavenUtilsTest {

    @Test
    public void testGet() {
        String output = MavenUtils.getDep(new File(""));
        assertNotNull(output);
        System.out.println(output);
    }
}