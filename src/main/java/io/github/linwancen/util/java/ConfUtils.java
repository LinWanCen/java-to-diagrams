package io.github.linwancen.util.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class ConfUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ConfUtils.class);

    private ConfUtils() {}

    public static Properties loadUtf8(String path) {
        return loadUtf8(new File(FileUtils.CLASS_PATH, path));
    }

    public static Properties loadUtf8(File file) {
        Properties prop = new Properties();
        String path = FileUtils.canonicalPath(file);
        if (!file.exists()) {
            LOG.warn("not found prop\tfile:///{}", path);
            return prop;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            prop.load(br);
            int size = prop.size();
            LOG.debug("load prop success, current size:{}\tfile:///{}", size, path);
        } catch (Exception e) {
            LOG.warn("load prop Exception\tfile:///{}", path, e);
        }
        return prop;
    }

}
