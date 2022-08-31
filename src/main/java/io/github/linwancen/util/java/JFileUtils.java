package io.github.linwancen.util.java;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class JFileUtils {

    private JFileUtils() {}

    public static File[] chooser() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setMultiSelectionEnabled(true);
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        File lastSelectFile = new File("lastSelectFile.txt");
        if (lastSelectFile.exists()) {
            String[] split = FileUtils.read(lastSelectFile).split("\n");
            if (split.length >= 2) {
                jFileChooser.setCurrentDirectory(new File(split[0]));
                ArrayList<File> files = new ArrayList<>();
                for (int i = 1; i < split.length; i++) {
                    files.add(new File(split[i]));
                }
                jFileChooser.setSelectedFiles(files.toArray(new File[0]));
            }
        } else {
            jFileChooser.setCurrentDirectory(new File("").getAbsoluteFile());
        }
        jFileChooser.showOpenDialog(null);
        File[] selectedFiles = jFileChooser.getSelectedFiles();
        if (selectedFiles.length > 0) {
            StringBuilder sb = new StringBuilder(jFileChooser.getCurrentDirectory().getAbsolutePath());
            for (File selectedFile : selectedFiles) {
                sb.append("\n").append(selectedFile.getPath());
            }
            FileUtils.write(lastSelectFile, sb.toString());
        }
        return selectedFiles;
    }
}