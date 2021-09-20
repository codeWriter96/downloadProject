package com.wang.downloadProject.utils;

import javax.swing.*;

public class FilePath {
    public static String getPath(){
        String path = null;
        //通过JFileChooser打开本地文件夹
        JFileChooser chooser = new JFileChooser("D:\\");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //打开选择器面板
        int returnVal = chooser.showSaveDialog(new JPanel());
        //保存文件从这里入手，输出的是文件名
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            path = chooser.getSelectedFile().getPath();
        }
        return path;
    }
}
