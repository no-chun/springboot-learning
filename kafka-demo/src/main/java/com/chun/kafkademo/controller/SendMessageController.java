package com.chun.kafkademo.controller;

import com.chun.kafkademo.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendMessageController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final KafkaTemplate<String, Message> kafkaTemplate;

    public SendMessageController(KafkaTemplate<String, Message> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/send")
    public void send(@RequestParam("message") String message) {
        Message msg = new Message("admin", message);
        ListenableFuture<SendResult<String, Message>> future = this.kafkaTemplate.send("test", msg);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.error("消息：{} 发送失败，原因：{}", msg, throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Message> result) {
                LOGGER.info("消息：{} 发送成功，offset=[{}]", msg, result.getRecordMetadata().offset());
            }
        });
    }
}
