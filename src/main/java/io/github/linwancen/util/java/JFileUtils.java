package io.github.linwancen.util.java;

import javax.swing.*;
import java.io.File;

public class JFileUtils {

    private JFileUtils() {}

    public static File[] chooser() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setMultiSelectionEnabled(true);
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        File lastSelectFile = new File("lastSelectFile.txt");
        String pathName = lastSelectFile.exists() ? FileUtils.read(lastSelectFile) : "";
        jFileChooser.setCurrentDirectory(new File(pathName).getAbsoluteFile());
        jFileChooser.showOpenDialog(null);
        File[] selectedFiles = jFileChooser.getSelectedFiles();
        if (selectedFiles.length > 0) {
            FileUtils.write(lastSelectFile, selectedFiles[0].getAbsolutePath());
        }
        return selectedFiles;
    }
}
