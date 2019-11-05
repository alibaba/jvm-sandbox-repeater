package com.alibaba.jvm.sandbox.repeater.plugin.core.serialize;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link }
 * <p>
 *
 * @author zhaoyb1990
 */
public class HessianSerializerTest {

    @Test
    public void serialize() {
        HessianSerializeDomain domain = new HessianSerializeDomain();
        Serializer serializer = SerializerProvider.instance().provide(Serializer.Type.HESSIAN);
        String sequence;
        try {
            sequence = serializer.serialize2String(domain);
            Assert.assertNotNull(sequence);
            HessianSerializeDomain deserialize = serializer.deserialize(sequence, HessianSerializeDomain.class);
            Assert.assertEquals(domain, deserialize);
        } catch (SerializeException e) {
            Assert.fail(e.getMessage());
        }
    }
}