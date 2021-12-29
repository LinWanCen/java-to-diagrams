package io.github.linwancen.util.maven;

import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertNotNull;

public class MavenUtilsTest {

    @Test
    public void testGet() {
        File file = new File("");
        File pomFile = MavenUtils.parentPomFile(file);
        String output = MavenUtils.getDep(pomFile);
        assertNotNull(output);
        System.out.println(output);
    }
}