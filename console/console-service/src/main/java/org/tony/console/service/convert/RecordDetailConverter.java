package org.tony.console.service.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.Serializer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializerProvider;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.RecordWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.tony.console.common.domain.InvocationBO;
import org.tony.console.common.domain.RecordDetailBO;
import org.tony.console.common.domain.RecordType;
import org.tony.console.db.model.Record;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link }
 * <p>
 *
 * @author zhaoyb1990
 */
@Component("recordDetailConverter")
@Slf4j
public class RecordDetailConverter implements ModelConverter<Record, RecordDetailBO> {

    @Resource
    private ModelConverter<Invocation, InvocationBO> invocationConverter;

    @Override
    public RecordDetailBO convert(Record source) {
        RecordDetailBO rdb = new RecordDetailBO();
        // lazy mode , this isn't a correct way to copy properties.
        BeanUtils.copyProperties(source, rdb);

        if (StringUtils.isNotEmpty(source.getType())) {
            rdb.setRecordType(RecordType.getByString(source.getType()));
        } else {
            if (rdb.getEntranceDesc().startsWith("java")){
                rdb.setRecordType(RecordType.JAVA);
            } else {
                rdb.setRecordType(RecordType.HTTP);
            }
        }

        Serializer hessian = SerializerProvider.instance().provide(Serializer.Type.HESSIAN);
        try {
            RecordWrapper wrapper = hessian.deserialize(source.getWrapperRecord(), RecordWrapper.class);
            rdb.setSubInvocations(
                            Optional.ofNullable(wrapper.getSubInvocations())
                                    .orElse(Collections.emptyList())
                                    .stream().map(invocationConverter::convert)
                                    .collect(Collectors.toList())

            );
        } catch (SerializeException e) {
            log.error("error deserialize record wrapper", e);
        }

        if (RecordType.HTTP.equals(rdb.getRecordType())) {
            List<HashMap> requestObj = JSON.parseArray(rdb.getRequest(), HashMap.class);
            if (requestObj!=null && requestObj.size()>=1) {
               HashMap r = requestObj.get(0);
               if (r.containsKey("contentType") && r.get("contentType")!=null) {
                   String contentType = (String) r.get("contentType");
                   String body = (String) r.get("body");
                   if (contentType.contains("application/json") && StringUtils.isNotEmpty(body)) {
                       if (body.startsWith("{")) {
                           r.put("body", JSON.parseObject(body));
                       }

                       if (body.startsWith("[")) {
                           r.put("body", JSON.parseArray(body));
                       }

                   }
               }
            }
            rdb.setRequestObj(requestObj.toArray());
        }

        if (RecordType.JAVA.equals(rdb.getRecordType())) {
            List<HashMap> requestObj = JSON.parseArray(rdb.getRequest(), HashMap.class);
            rdb.setRequestObj(requestObj.toArray());
        }

        try {
            rdb.setResponseObj(JSON.parse(rdb.getResponse()));
            rdb.setResponseType("json");
        } catch (Exception e) {
            rdb.setResponseType("String");
        }


        return rdb;
    }

    @Override
    public List<RecordDetailBO> convert(List<Record> records) {
        return null;
    }

    @Override
    public List<Record> reconvertList(List<RecordDetailBO> sList) {
        return null;
    }

    @Override
    public Record reconvert(RecordDetailBO target) {
        return null;
    }

    public static void main(String args[]) {
        String req = "[{\"batchOrderNo\":\"618059134392674300\",\"createTime\":\"1682419991\",\"deliveryOrderNo\":\"780112304251013001\",\"deliveryOrderStatus\":\"flow\",\"estArriveDate\":null,\"expressCompany\":\"Posten\",\"expressDetail\":[],\"expressNo\":\"40170730258734001290\",\"goodsList\":[{\"dnItemNo\":null,\"productCode\":null,\"sapCode\":\"1011342\",\"skuCount\":1.0}],\"saleOrderNo\":\"619059134397674600\",\"sysSource\":null,\"warehouseName\":\"精品挪威总仓\",\"warehouseNo\":\"NORDC02\"}]";
        List<HashMap> requestObj = JSON.parseArray(req, HashMap.class);
        System.out.println(requestObj);
    }
}
