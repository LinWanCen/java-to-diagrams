package io.github.linwancen.util.maven;

import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static org.testng.Assert.*;

public class MavenUtilsTest {

    @Test
    public void testGet() {
        File file = new File("");
        File pomFile = MavenUtils.parentPomFile(file);
        List<String> depList = MavenUtils.getDep(pomFile);
        assertNotNull(depList);
        assertFalse(depList.isEmpty());
        for (String dep : depList) {
            System.out.println(dep);
        }
    }
}