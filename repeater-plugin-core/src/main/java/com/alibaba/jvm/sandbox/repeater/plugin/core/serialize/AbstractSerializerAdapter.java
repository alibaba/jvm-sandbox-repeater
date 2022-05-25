package com.alibaba.jvm.sandbox.repeater.plugin.core.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.google.common.io.BaseEncoding;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * {@link AbstractSerializerAdapter} 抽象的序列化实现，主要完成byte[]和字符串的转换动作
 * <p>
 *
 * @author zhaoyb1990
 */
public abstract class AbstractSerializerAdapter implements Serializer {

    @Override
    public String serialize2String(Object object) throws SerializeException {
        return serialize2String(object, null);
    }

    @Override
    public String serialize2String(Object object, ClassLoader classLoader) throws SerializeException {
        // byte -> sequence
        // 每次压缩之后base64的结果都不一样；会导致相似度匹配失效
        // return encode(serialize(object, classLoader));
        return BaseEncoding.base64().encode(serialize(object, classLoader));
    }

    @Override
    public byte[] serialize(Object object) throws SerializeException {
        return serialize(object, null);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws SerializeException {
        return bytes == null ? null : deserialize(bytes, type, null);
    }

    @Override
    public <T> T deserialize(String sequence, Class<T> type) throws SerializeException {
        return sequence == null ? null : deserialize(sequence, type, null);
    }

    @Override
    public <T> T deserialize(String sequence, Class<T> type, ClassLoader classLoader) throws SerializeException {
        // sequence -> byte
        // 每次压缩之后base64的结果都不一样；会导致相似度匹配失效
        // return deserialize(decode(sequence), type, classLoader);
        return sequence == null ? null : deserialize(BaseEncoding.base64().decode(sequence), type, classLoader);
    }

    @Override
    public Object deserialize(String sequence) throws SerializeException{
        return sequence == null ? null : deserialize(BaseEncoding.base64().decode(sequence));
    }

    /**
     * 解码成byte数组
     *
     * @param sequence 编码序列
     * @return byte数组
     * @throws SerializeException 序列化异常
     */
    private byte[] decode(String sequence) throws SerializeException {
        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        ZipInputStream zip = null;
        try {
            // base64会多占用存储空间
            byte[] bytes = BaseEncoding.base64().decode(sequence);
            /* byte[] bytes = sequence.getBytes(defaultCharset); */
            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(bytes);
            zip = new ZipInputStream(in);
            zip.getNextEntry();
            byte[] buffer = new byte[1024];
            int i;
            while ((i = zip.read(buffer)) > 0) {
                out.write(buffer, 0, i);
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new SerializeException("[Error-1003]-decode error", e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(zip);
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * zip压缩后转为字符串
     *
     * @param bytes byte数组
     * @return 压缩后字符串
     * @throws SerializeException 序列化异常
     */
    private String encode(byte[] bytes) throws SerializeException {
        ByteArrayOutputStream out = null;
        ZipOutputStream zip = null;
        try {
            out = new ByteArrayOutputStream();
            zip = new ZipOutputStream(out);
            zip.putNextEntry(new ZipEntry("0"));
            zip.write(bytes);
            zip.closeEntry();
            return BaseEncoding.base64().encode(out.toByteArray());
            /* return out.toString(defaultCharset); */
        } catch (Exception e) {
            throw new SerializeException("[Error-1004]-encode error", e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(zip);
        }
    }
}
