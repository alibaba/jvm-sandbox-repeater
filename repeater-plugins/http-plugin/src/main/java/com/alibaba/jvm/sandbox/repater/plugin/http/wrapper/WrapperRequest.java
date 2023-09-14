package com.alibaba.jvm.sandbox.repater.plugin.http.wrapper;

import com.alibaba.jvm.sandbox.repater.plugin.http.HttpStandaloneListener;
import com.alibaba.jvm.sandbox.repater.plugin.http.util.ContentDisposition;
import com.alibaba.jvm.sandbox.repater.plugin.http.util.FieCopyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
public class WrapperRequest extends HttpServletRequestWrapper {

    /**
     * 仅支持<1M大小的文件的录制&回放
     */
    public final static Long MAX_LEN = 1024*1024*2L;

    private final HttpServletResponse response;

    private final String body;

    private final HttpStandaloneListener listener;

    private final boolean usingBody;

    private boolean multipart;

    private byte[] fileContent;

    private String fileName;

    private String partName;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request 请求
     * @throws IOException if the request is null
     */
    public WrapperRequest(HttpServletRequest request, HttpServletResponse response, HttpStandaloneListener listener)
            throws IOException, ServletException {
        super(request);
        this.response = response;
        this.listener = listener;
        // application/json的方式提交，需要拦截body
        this.usingBody = StringUtils.contains(request.getContentType(), "application/json");
        this.multipart = StringUtils.containsIgnoreCase(request.getContentType(), "multipart/");

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        if (usingBody) {
            try {
                InputStream inputStream = request.getInputStream();
                if (inputStream != null) {
                    String ce = request.getCharacterEncoding();
                    if (StringUtils.isNotEmpty(ce)) {
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream, ce));
                    } else {
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    }
                    char[] charBuffer = new char[128];
                    int bytesRead;
                    while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                        stringBuilder.append(charBuffer, 0, bytesRead);
                    }
                }
            } finally {
                IOUtils.closeQuietly(bufferedReader);
            }
        }
        body = stringBuilder.toString();

        if (multipart) {
            Collection<Part> parts = request.getParts();
            if (parts.size() > 0) {
                Iterator var = parts.iterator();
                //本次只支持单文件上传，TO_DO 后面再支持多文件的
                while(var.hasNext()) {
                    Part part = (Part)var.next();
                    String headerValue = part.getHeader("Content-Disposition");
                    ContentDisposition disposition = ContentDisposition.parse(headerValue);
                    String filename = disposition.getFilename();

                    if ("file".equals(part.getName())) {
                        this.partName = part.getName();
                        this.fileName = filename;
                        this.fileContent = FieCopyUtils.copyToByteArray(part.getInputStream());

                        if (fileContent.length>MAX_LEN) {
                            throw new IOException("file is too long for this way");
                        }

                        break;
                    } else {
                        continue;
                    }
                }
            }

        }
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return startAsync(getRequest(), response);
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
            throws IllegalStateException {
        listener.onStartAsync(this);
        AsyncContext asyncContext = super.startAsync(this, response);
        asyncContext.addListener(new WrapperAsyncListener(listener, this, (WrapperResponseCopier) response));
        return asyncContext;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (usingBody) {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());

            return new ServletInputStream() {
                @Override
                public int read() {
                    return byteArrayInputStream.read();
                }
            };
        } else {
            return super.getInputStream();
        }
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (usingBody) {
            return new BufferedReader(new InputStreamReader(this.getInputStream()));
        } else {
            return super.getReader();
        }
    }

    public String getBody() {
        return this.body;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPartName() {
        return partName;
    }

    public boolean isMultipart() {
        return multipart;
    }

    public Map<String, Object> getMultipart() {
        if (multipart) {
            Map<String, Object> map = new HashMap<>();
            map.put("fileName", fileName);
            map.put("partName", partName);
            map.put("fileContent", fileContent);
            return map;
        }

        return null;
    }
}
