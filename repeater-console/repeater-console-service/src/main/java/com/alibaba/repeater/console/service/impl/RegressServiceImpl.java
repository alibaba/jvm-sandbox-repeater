package com.alibaba.repeater.console.service.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.core.util.ExecutorInner;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.Regress;
import com.alibaba.repeater.console.service.RegressService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link RegressServiceImpl} 回归用demo服务
 * <p>
 *
 * @author zhaoyb1990
 */
@Service("regressService")
public class RegressServiceImpl implements RegressService {

    private AtomicInteger sequence = new AtomicInteger(0);

    private String[] partners = new String[]{"韩梅梅", "李莉", "吉姆", "小红", "张三", "李四", "王麻子"};
    private String[] slogans = new String[]{"JAVA", "Python", "PHP", "C#", "C++", "Javascript", "GO"};

    @Override
    public RepeaterResult<Regress> getRegress(String name) {
        return RepeaterResult.builder()
                .data(getRegressInner(name, 1))
                .success(true)
                .message("operate success")
                .build();
    }

    @Override
    public RepeaterResult<List<Regress>> getRegress(final String name, int count) {
        List<Regress> regresses = Lists.newArrayList();
        for (int i = 0; i < count; i++) {
            final int index = i;
            Future<Regress> future = ExecutorInner.submit(new Callable<Regress>() {
                @Override
                public Regress call() throws Exception {
                    return getRegressInner(name, index);
                }
            });
            try {
                regresses.add(future.get());
            } catch (Exception e) {
                return RepeaterResult.builder()
                        .success(false)
                        .message(e.getMessage())
                        .build();
            }
        }
        return RepeaterResult.builder()
                .data(regresses)
                .success(true)
                .message("operate success")
                .build();
    }

    @Override
    public RepeaterResult<String> findPartner(String name) {
        return RepeaterResult.builder().success(true).message("配对成功").data(partners[sequence.getAndIncrement() % partners.length]).build();
    }

    @Override
    public String slogan() {
        return slogans[sequence.getAndIncrement() % slogans.length] + "是世界上最好的语言!";
    }

    private Regress getRegressInner(String name, Integer index) {
        Regress regress = new Regress();
        regress.setIndex(index + sequence.incrementAndGet());
        regress.setName(name);
        regress.setTimestamp(System.currentTimeMillis());
        return regress;
    }

}
