package com.alibaba.repeater.console.start.Utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CSVUtil {

    /**
     * 解析CSV格式数据文件
     * @param file 测试数据文件
     * @return CSVParse 解析实例
     * @throws IOException IO异常
     */
    public static CSVParser parserCSV(File file) throws IOException {
        CSVFormat formator = CSVFormat.DEFAULT.withHeader();
        FileReader fileReader = new FileReader(file);
        return new CSVParser(fileReader, formator);
    }
}
