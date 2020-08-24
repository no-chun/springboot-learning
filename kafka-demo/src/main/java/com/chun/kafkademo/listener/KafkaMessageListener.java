package com.chun.kafkademo.listener;

import com.chun.kafkademo.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageListener {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @KafkaListener(topics = "test", groupId = "test-consumer")
    public void listen(Message message) {
        LOGGER.info("接收消息: {}", message);
    }
}
