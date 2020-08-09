package com.alibaba.repeater.console.service.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SSHResult {
    int errorCode;
    String stdOutput;
    String errorOutput;

    @Override
    public String toString() {
        return "SSHResult{" +
                "errorCode=" + errorCode +
                ", stdOutput='" + stdOutput + '\'' +
                ", errorOutput='" + errorOutput + '\'' +
                '}';
    }
}