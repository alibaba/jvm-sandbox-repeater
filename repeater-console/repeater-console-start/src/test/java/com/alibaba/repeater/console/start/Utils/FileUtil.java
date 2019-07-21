package com.alibaba.repeater.console.start.Utils;

import org.apache.commons.lang3.StringUtils;
import java.io.File;

public class FileUtil {

    /**
     * 查找到指定的Data文件
     * @param folder 待搜索的文件目录
     * @param fileName 文件名
     * @return 已匹配文件
     */
    private static File findFileInFolder(File folder, String fileName) {
        if (StringUtils.isEmpty(fileName.trim()) || null == folder || !folder.exists()) {
            return null;
        }
        File[] files = folder.listFiles();
        if(files != null){
            for (File file : files) {
                if (file.isFile() && file.getName().equals(fileName)) {
                    return file;
                } else if (file.isDirectory()) {
                    File file2 = findFileInFolder(file, fileName);
                    if (null != file2) {
                        return file2;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取当前目录
     * @param fileName 文件名称
     * @return 已匹配文件(递归式搜索)
     */
    public static File findFileInCurrentProject(String fileName) {
        return findFileInFolder(new File(System.getProperty("user.dir")).getAbsoluteFile(), fileName);
    }
}
