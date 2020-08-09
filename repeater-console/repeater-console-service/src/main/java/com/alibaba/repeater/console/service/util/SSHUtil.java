package com.alibaba.repeater.console.service.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SSHUtil {
    public static SSHResult runCommand(String ip, String port, String userName, String password, String keyFile, String command) {
        SSHResult sshResult = new SSHResult();
        List<String> cmdList = new ArrayList<>();
        cmdList.add("ssh");
        if(StringUtils.isNotBlank(port)) {
            cmdList.add("-p");
            cmdList.add(port);
        }

        if(StringUtils.isNotBlank(password)) {
            cmdList.add("-P");
            cmdList.add(password);
        }

        if(StringUtils.isNotBlank(keyFile)) {
            cmdList.add("-i");
            cmdList.add(keyFile);
        }

        cmdList.add(userName + "@" + ip);
        cmdList.add(command);

        ProcessBuilder pb = new ProcessBuilder(cmdList);
        System.out.println("Run echo command:"  + cmdList);
        Process process = null;
        try {
            process = pb.start();
            int errCode = process.waitFor();
            System.out.println("Echo command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
            String stdOutput = getOutput(process.getInputStream());
            String errorOutput = getOutput(process.getErrorStream());
            System.out.println("Std Output:\n" + stdOutput);
            System.out.println("Error Output:\n" + errorOutput);

            sshResult.setErrorCode(errCode);
            sshResult.setStdOutput(stdOutput);
            sshResult.setErrorOutput(errorOutput);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return sshResult;
    }

    private static String getOutput(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream, "gbk"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + System.getProperty("line.separator"));
            }
        } finally {
            br.close();
        }
        return sb.toString();
    }

    public static void main(String[] args) {
//        String cmd = "curl -s http://sandbox-ecological.oss-cn-hangzhou.aliyuncs.com/install-repeater.sh | bash";
        String cmd = "sed -i 's/127.0.0.1:8001/192.168.2.4:8001/g' ~/.sandbox-module/cfg/repeater.properties";
        SSHResult sshResult = runCommand("192.168.43.28", "10122", "root", "", "C:\\Users\\zgq\\code\\research\\zxh\\gs-rest-service\\complete\\docker\\id_rsa", cmd);
        System.out.println("sshResult:" + sshResult);
    }
}


