package com.alibaba.repeater.console.start.DataProvider;

import com.alibaba.repeater.console.start.Utils.CSVUtil;
import com.alibaba.repeater.console.start.Utils.FileUtil;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Preconditions;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HttpPluginTestDataProvider {

    @DataProvider(name="HttpPluginTestDataProvider")
    public static Object[][] getHttpPluginTestData() throws IOException {
        List<String> dataBeans= Lists.newArrayList();
        String dataFileName="HttpPluginRegressTestData.csv";
        File dataFile = FileUtil.findFileInCurrentProject(dataFileName);
        CSVParser parser = CSVUtil.parserCSV(dataFile);
        for(CSVRecord dataRecord : parser){
            String url=dataRecord.get("url");
            if(! StringUtils.isBlank(url)){
                dataBeans.add(url);
            }
        }
        Preconditions.checkNotNullOrEmpty(dataBeans.toArray());
        int size=dataBeans.size();
        Object[][] testData=new Object [size][1];
        //填充数据集
        for (int i = 0; i < size; i++) {
            testData[i][0] = dataBeans.get(i);
        }
        return testData;
    }
}
