package com.alibaba.jvm.sandbox.repater.plugin.http.wrapper;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
public class WrapperOutputStreamCopier extends ServletOutputStream {

    private OutputStream out;
    private ByteArrayOutputStream copier;

    WrapperOutputStreamCopier(OutputStream out) {
        this.out = out;
        this.copier = new ByteArrayOutputStream(1024);
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        copier.write(b);
    }

    byte[] getCopy() {
        return copier.toByteArray();
    }
}