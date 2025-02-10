package com.yuzhihao.websocket;

import com.alibaba.fastjson2.JSONObject;
import com.yuzhihao.websocket.frame.RedisPubSubMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * redis处理分布式websocket通信消息bean
 *
 * @author yuzhihao
 * @since 2025-01-22 11:37:32
 */
@Slf4j
@Data
public class RedisWebsocketMessage implements RedisPubSubMessage, Serializable {

    private String key;

    private JSONObject message;

    public RedisWebsocketMessage() {
    }

    public RedisWebsocketMessage(String key, JSONObject message) {
        this.key = key;
        this.message = message;
    }

    @Override
    public String code() {
        return "RedisWebsocketMessage";
    }

}
