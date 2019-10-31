package com.caucho.hessian.io;

import com.caucho.hessian.HessianException;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * {@link BigDecimalSerializerFactory}
 * <p>
 *
 * @author zhaoyb1990
 */
public class BigDecimalSerializerFactory extends AbstractSerializerFactory {

    private Serializer serializer;

    private Deserializer deserializer;

    public BigDecimalSerializerFactory() {
        this.serializer = new BigDecimalSerializer();
        this.deserializer = new BigDecimalDeserializer();
    }

    @Override
    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        if (BigDecimal.class.isAssignableFrom(cl)) {
            return this.serializer;
        }
        return null;
    }

    @Override
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        if (BigDecimal.class.isAssignableFrom(cl)) {
            return this.deserializer;
        }
        return null;
    }

    class BigDecimalSerializer extends AbstractSerializer {

        @Override
        public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
            if (null == obj) {
                return;
            }
            if (out.addRef(obj)) {
                return;
            }
            Class<?> cl = obj.getClass();
            BigDecimal bigDecimal = (BigDecimal) obj;
            out.writeMapBegin(cl.getName());
            out.writeString("value");
            out.writeString(String.valueOf(bigDecimal));
            out.writeMapEnd();
        }
    }

    class BigDecimalDeserializer extends AbstractDeserializer {

        @Override
        public Class<?> getType() {
            return BigDecimal.class;
        }

        @Override
        public Object readMap(AbstractHessianInput in) throws IOException {
            String value = null;
            while (!in.isEnd()) {
                String key = in.readString();
                if ("value".equals(key)) {
                    value = in.readString();
                } else {
                    in.readObject();
                }
            }
            in.readMapEnd();
            Object object = create(value);
            in.addRef(object);
            return object;
        }

        @Override
        public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
            String value = null;
            for (String filedName : fieldNames) {
                if ("value".equals(filedName)) {
                    value = in.readString();
                } else {
                    in.readObject();
                }
            }
            Object object = create(value);
            in.addRef(object);
            return object;
        }

        private Object create(String value) throws IOException {
            try {
                return new BigDecimal(value);
            } catch (Exception e) {
                throw new HessianException(BigDecimal.class.getName() + ": value=" + value, e);
            }
        }
    }
}
