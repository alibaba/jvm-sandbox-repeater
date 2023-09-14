package org.tony.console.service.trxMsg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.db.mapper.TrxMessageMapper;
import org.tony.console.service.convert.TrxMsgConvert;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/3/27 09:56
 */
@Slf4j
@Component
public class TrxMsgService {

    private volatile static Map<Topic, List<TrxMsgListener>> msgListenerMap;

    @Autowired
    List<TrxMsgListener> listenerList;

    private final int MaxExecTime = 3;

    static {
        msgListenerMap = new HashMap<>();
    }

    @Resource
    TrxMsgConvert trxMsgConvert;

    @Resource
    TrxMessageMapper trxMsgMapper;


    public void publishMsg(TrxMsg trxMsg) {
        if (trxMsg.getGmtExec()==null) {
            trxMsg.setGmtExec(new Date());
        }

        trxMsg.setGroup(Group.DEFAULT);
        trxMsg.setExecTimes(0);
        trxMsg.setMsgStatus(MsgStatus.INIT);


        trxMsgMapper.insert(trxMsgConvert.reconvert(trxMsg));
    }

    public void execMsg(TrxMsg trxMsg) {
        List<TrxMsgListener> filterList = getListenerList(trxMsg.getTopic());

        if (CollectionUtils.isEmpty(filterList)) {
            return;
        }

        for (TrxMsgListener listener : filterList) {
            try {
                ExecResult result = listener.execute(trxMsg.getContent());

                if (ExecResult.FAIL.equals(result)) {
                    if (trxMsg.getExecTimes()>=(MaxExecTime-1)) {
                        trxMsg.setMsgStatus(MsgStatus.FAIL);
                    } else {
                        trxMsg.setMsgStatus(MsgStatus.FAIL_TO_RETRY);
                    }

                    update(trxMsg);
                    return;
                }
            } catch (Exception e) {
                log.error("system error", e);
                return;
            }
        }

        //成功执行，直接删除就行了
        trxMsgMapper.deleteById(trxMsg.getId());
    }

    private void update(TrxMsg trxMsg) {
        trxMsgMapper.update(trxMsgConvert.reconvert(trxMsg));
    }


    private List<TrxMsgListener> getListenerList(Topic topic) {
        if (msgListenerMap.containsKey(topic)) {
            return msgListenerMap.get(topic);
        } else {
            List<TrxMsgListener> msgList = listenerList.stream().filter(item->item.getTopic().equals(topic)).collect(Collectors.toList());
          synchronized (TrxMsgService.class)  {
              msgListenerMap.put(topic, msgList);
          }
          return msgList;
        }
    }
}
