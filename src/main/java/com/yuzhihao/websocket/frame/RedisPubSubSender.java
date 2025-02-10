package com.yuzhihao.websocket.frame;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * redis发布订阅 消息发送者
 *
 * @author yuzhihao
 */
@Component
@AllArgsConstructor
public class RedisPubSubSender {

    private final StringRedisTemplate template;

    private static StringRedisTemplate STRING_REDIS_TEMPLATE;

    @PostConstruct
    public void init() {
        STRING_REDIS_TEMPLATE = template;
    }

    /**
     * 发布订阅消息
     *
     * @param message
     */
    public static void send(RedisPubSubMessage message) {
        try {
            STRING_REDIS_TEMPLATE.convertAndSend(message.code(), RedisPubSubListener.OBJECT_MAPPER.writer().writeValueAsString(message));
        } catch (Exception e) {
            throw new RuntimeException("发布订阅消息失败：" + e.getMessage(), e);
        }
    }

}
