package io.github.linwancen.util.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class FileUtils {
    static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {}

    public static final String CLASS_PATH;

    public static final Pattern PATH_PATTERN = Pattern.compile("[" + File.pathSeparator + "\r\n]+");

    public static String[] split(String paths) {
        return PATH_PATTERN.split(paths);
    }

    static {
        @SuppressWarnings("ConstantConditions")
        String path = ClassLoader.getSystemClassLoader().getResource("").getPath();
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (Exception e) {
            LOG.error("path = URLDecoder.decode(path, \"UTF-8\"); path :\t{}", path, e);
        }
        CLASS_PATH = path;
    }

    public static String read(File file) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.getAbsoluteFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void write(File file, String text) {
        try {
            Files.write(file.toPath(), text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 递归遍历文件（先绝对路径）
     * <br>避免相对路径 listFiles 为 null
     */
    public static void deep(Consumer<File> handle, FileFilter filter, File... files) {
        files = Arrays.stream(files).map(File::getAbsoluteFile).toArray(File[]::new);
        loop(handle, filter, files);
    }

    /**
     * 递归遍历文件
     */
    private static void loop(Consumer<File> handle, FileFilter filter, File... files) {
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isFile()) {
                handle.accept(f);
                continue;
            }
            loop(handle, filter, f.listFiles(filter));
        }
    }

    public static String canonicalPath(File file) {
        try {
            return file.getCanonicalPath().replace('\\', '/');
        } catch (IOException e) {
            String path = file.getAbsolutePath().replace('\\', '/');
            LOG.warn("getCanonicalPath IOException, use AbsolutePath\tfile:///{}", path, e);
            return path;
        }
    }

    /**
     * 避免没有文件夹报错
     */
    public static void mkdir(File dir) {
        if (dir != null && !dir.exists() && dir.mkdirs()) {
            String path = canonicalPath(dir);
            LOG.debug("mkdir fail\tfile:///{}", path);
        }
    }
}
