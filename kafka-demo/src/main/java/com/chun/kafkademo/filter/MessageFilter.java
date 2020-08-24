package com.chun.kafkademo.filter;

import com.chun.kafkademo.model.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.stereotype.Component;

@Component
public class MessageFilter implements RecordFilterStrategy<String, Message> {
    @Override
    public boolean filter(ConsumerRecord<String, Message> consumerRecord) {
        return consumerRecord.value().getMsg().contains("fuck");
    }
}
