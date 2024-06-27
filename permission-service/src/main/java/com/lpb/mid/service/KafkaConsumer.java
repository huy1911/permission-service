package com.lpb.mid.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lpb.mid.dto.JWTDto;
import com.lpb.mid.dto.SendKafkaDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {
    @Autowired
    private PermissionService permissionService;
    @KafkaListener(topics = "${kafka.consumer.check_token.topic}", groupId = "${kafka.consumer.check_token.group_Id_red}",
            errorHandler = "voidSendToErrorHandler",
            containerFactory = "kafkaListenerCusContainerFactory",
            concurrency = "20")
    @SendTo()
    public Message<?> ConsumerRecord(ConsumerRecord<String, Object> consumerRecord) {
        try {
            log.info("redConsumer : red message message ----> {}", consumerRecord);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String resConsumer = String.valueOf(consumerRecord.value());
            SendKafkaDto accRequestDTO = objectMapper.readValue(resConsumer, SendKafkaDto.class);
            log.info("ConsumerRecord : request read kafka ----->{}", accRequestDTO);
            JWTDto responseDto = permissionService.getDtoLogin(accRequestDTO);
            String messageCheckTran = objectMapper.writeValueAsString(responseDto);
            log.info("redConsumerAcc : response ---> {} by key ---->{}", messageCheckTran, consumerRecord.key());
            return MessageBuilder.withPayload(messageCheckTran)
                    .setHeader(KafkaHeaders.MESSAGE_KEY, consumerRecord.key())
                    .setHeader(KafkaHeaders.CORRELATION_ID, consumerRecord.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value())
                    .setHeader(KafkaHeaders.TOPIC, consumerRecord.headers().lastHeader(KafkaHeaders.REPLY_TOPIC).value())
                    .build();
        } catch (Exception e) {
            log.error("redConsumer : red message fail ----->{}", e.getMessage());
            return MessageBuilder.withPayload("null")
                    .setHeader(KafkaHeaders.MESSAGE_KEY, consumerRecord.key())
                    .setHeader(KafkaHeaders.CORRELATION_ID, consumerRecord.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value())
                    .setHeader(KafkaHeaders.TOPIC, consumerRecord.headers().lastHeader(KafkaHeaders.REPLY_TOPIC).value())
                    .build();
        }

    }
}
