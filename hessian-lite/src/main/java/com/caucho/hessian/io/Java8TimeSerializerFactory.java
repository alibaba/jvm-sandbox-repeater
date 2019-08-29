package com.caucho.hessian.io;


import com.caucho.hessian.io.java8time.*;
import com.caucho.hessian.io.java8time.LocaleHandle;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link Java8TimeSerializerFactory}
 * <p>
 *
 * @author zhaoyb1990
 */
public class Java8TimeSerializerFactory extends AbstractSerializerFactory {

    private Map<Class<?>, Serializer> serializerMap = new HashMap<Class<?>, Serializer>();

    {
        try {
            serializerMap.put(Class.forName("java.time.LocalTime"), Java8TimeSerializer.create(LocalTimeHandle.class));
            serializerMap.put(Class.forName("java.time.LocalDate"), Java8TimeSerializer.create(LocalDateHandle.class));
            serializerMap.put(Class.forName("java.time.LocalDateTime"), Java8TimeSerializer.create(LocalDateTimeHandle.class));

            serializerMap.put(Class.forName("java.time.Instant"), Java8TimeSerializer.create(InstantHandle.class));
            serializerMap.put(Class.forName("java.time.Duration"), Java8TimeSerializer.create(DurationHandle.class));
            serializerMap.put(Class.forName("java.time.Period"), Java8TimeSerializer.create(PeriodHandle.class));

            serializerMap.put(Class.forName("java.time.Year"), Java8TimeSerializer.create(YearHandle.class));
            serializerMap.put(Class.forName("java.time.YearMonth"), Java8TimeSerializer.create(YearMonthHandle.class));
            serializerMap.put(Class.forName("java.time.MonthDay"), Java8TimeSerializer.create(MonthDayHandle.class));

            serializerMap.put(Class.forName("java.time.OffsetDateTime"), Java8TimeSerializer.create(OffsetDateTimeHandle.class));
            serializerMap.put(Class.forName("java.time.ZoneOffset"), Java8TimeSerializer.create(ZoneOffsetHandle.class));
            serializerMap.put(Class.forName("java.time.OffsetTime"), Java8TimeSerializer.create(OffsetTimeHandle.class));
            serializerMap.put(Class.forName("java.time.ZonedDateTime"), Java8TimeSerializer.create(ZonedDateTimeHandle.class));

            serializerMap.put(Class.forName("java.util.Locale"), Java8TimeSerializer.create(LocaleHandle.class));
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        return serializerMap.get(cl);
    }

    @Override
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        return null;
    }
}