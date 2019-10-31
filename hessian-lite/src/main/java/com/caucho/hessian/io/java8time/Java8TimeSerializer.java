package com.caucho.hessian.io.java8time;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * {@link Java8TimeSerializer}
 * <p>
 *
 * @author zhaoyb1990
 */
public class Java8TimeSerializer<T> extends AbstractSerializer {

    private Class<T> handleType;

    private Java8TimeSerializer(Class<T> handleType) {
        this.handleType = handleType;
    }

    public static <T> Java8TimeSerializer<T> create(Class<T> handleType) {
        return new Java8TimeSerializer<T>(handleType);
    }

    @Override
    public void writeObject(Object obj, AbstractHessianOutput out)
            throws IOException {
        if (obj == null) {
            out.writeNull();
            return;
        }
        T handle;
        try {
            Constructor<T> constructor = this.handleType.getConstructor(Object.class);
            handle = constructor.newInstance(obj);
        } catch (Exception e) {
            throw new RuntimeException("the class :" + this.handleType.getName() + " construct failed:" + e.getMessage(), e);
        }
        out.writeObject(handle);
    }
}
