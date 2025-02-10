package com.yuzhihao.websocket.frame;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.util.Locale;

/**
 * redis消息队列配置-订阅者
 * @author 网络worker
 */
@Slf4j
public abstract class RedisPubSubListener<T> implements MessageListener {

    public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setLocale(Locale.CHINA);
        // 配置 ObjectMapper 以包含类型信息
        TypeResolverBuilder<?> typer = new StdTypeResolverBuilder().init(JsonTypeInfo.Id.CLASS, null).inclusion(JsonTypeInfo.As.PROPERTY);
        OBJECT_MAPPER.setDefaultTyping(typer);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            this.onMessage(OBJECT_MAPPER.readValue(message.getBody(), new TypeReference<T>() {}));
        } catch (Exception e) {
            this.failHandler(message,e);
        }
    }

    /**
     * 处理器
     * 当有新消息到达时，此方法被调用以处理消息
     *
     * @param message 消息体，包含延时消息的数据和元信息
     */
    public abstract void onMessage(T message);

    /**
     * 处理消息投递异常
     * 当消息投递过程中发生异常时，此方法被调用用于处理该异常
     *
     * @param message   消息，发生异常的消息对象
     * @param throwable 异常，投递过程中遇到的异常对象
     */
    public void failHandler(Message message, Throwable throwable) {
        log.error("订阅异常：{}", message, throwable);
    }

}

