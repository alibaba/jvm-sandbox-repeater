package com.alibaba.jvm.sandbox.repeater.plugin.core.serialize;

import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.MockInvocation;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @Test
    public void deserialize() {
        HessianSerializeDomainSub domain = new HessianSerializeDomainSub();
        Serializer serializer = SerializerProvider.instance().provide(Serializer.Type.HESSIAN);
        String sequence;
        try {
            sequence = serializer.serialize2String(domain);
            Assert.assertNotNull(sequence);
            HessianSerializeDomainSub deserialize = serializer.deserialize(sequence, HessianSerializeDomainSub.class);
            Assert.assertEquals(domain, deserialize);
        } catch (SerializeException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void serializeSpec() {
        List<MockInvocation> list = new ArrayList<>();
        MockInvocation m1 = new MockInvocation();
        MockInvocation m2 = new MockInvocation();
        list.add(m1);
        list.add(m2);

        Object[] org = new Object[1];
        org[0] = new GregorianCalendar();
        m1.setCurrentArgs(org);

        Object xxx = new Object();

        Map<String, Object> map = new HashMap<>();
        map.put("xx", xxx);
        map.put("jj", xxx);
        Object[] ol = new Object[1];
        ol[0] = map;
        m2.setCurrentArgs(ol);

        try {
            String ens = SerializerWrapper.hessianSerialize(list);
            SerializerWrapper.hessianDeserialize(ens);
        } catch (SerializeException e) {
            Assert.assertNull(e);
        }
        Assert.assertTrue(true);
    }
}