package org.tony.console.service.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author peng.hu1
 * @Date 2022/12/7 10:04
 */
@Component
public class KafkaEnvConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    public String server;

    @Value("${kafka.user}")
    public String username;

    @Value("${kafka.password}")
    public String password;

    @Value("${kafka.topic.record}")
    public String recordTopic;

    @Value("${kafka.topic.replay}")
    public String replayTopic;
}
