package org.tony.console.service.convert;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.db.model.TrxMsgDO;
import org.tony.console.service.trxMsg.Group;
import org.tony.console.service.trxMsg.MsgStatus;
import org.tony.console.service.trxMsg.Topic;
import org.tony.console.service.trxMsg.TrxMsg;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/3/27 10:44
 */
@Component
public class TrxMsgConvert implements ModelConverter<TrxMsgDO, TrxMsg>{

    @Override
    public TrxMsg convert(TrxMsgDO source) {
        TrxMsg trxMsg = new TrxMsg();

        trxMsg.setId(source.getId());
        trxMsg.setMsgStatus(MsgStatus.get(source.getStatus()));
        trxMsg.setExecTimes(source.getExecTime());
        trxMsg.setGmtCreate(source.getGmtCreate());
        trxMsg.setGmtExec(source.getGmtExec());
        trxMsg.setGmtUpdate(source.getGmtUpdate());
        trxMsg.setGroup(Group.DEFAULT);
        trxMsg.setTopic(Topic.valueOf(source.getTopic()));
        trxMsg.setContent(JSON.parse(source.getContent(), JSONReader.Feature.SupportAutoType));

        return trxMsg;
    }

    @Override
    public List<TrxMsg> convert(List<TrxMsgDO> trxMsgDOS) {
        if (CollectionUtils.isEmpty(trxMsgDOS)) {
            return new ArrayList<>(0);
        }

        return trxMsgDOS.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public List<TrxMsgDO> reconvertList(List<TrxMsg> sList) {
        if (CollectionUtils.isEmpty(sList)) {
            return new ArrayList<>(0);
        }

        return sList.stream().map(this::reconvert).collect(Collectors.toList());
    }

    @Override
    public TrxMsgDO reconvert(TrxMsg target) {
        TrxMsgDO trxMsgDO = new TrxMsgDO();

        trxMsgDO.setId(target.getId());
        trxMsgDO.setContent(JSON.toJSONString(target.getContent(), JSONWriter.Feature.WriteClassName));
        trxMsgDO.setExecTime(target.getExecTimes());
        trxMsgDO.setGmtCreate(target.getGmtCreate());
        trxMsgDO.setGroup(Group.DEFAULT.name());
        trxMsgDO.setGmtExec(target.getGmtExec());
        trxMsgDO.setTopic(target.getTopic().name());
        trxMsgDO.setGmtUpdate(target.getGmtUpdate());
        trxMsgDO.setStatus(target.getMsgStatus().code);

        return trxMsgDO;
    }
}
