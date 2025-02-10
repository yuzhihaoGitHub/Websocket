package com.yuzhihao.websocket;

import com.alibaba.fastjson2.JSONObject;
import com.yuzhihao.websocket.frame.RedisPubSubSender;
import com.yuzhihao.websocket.socket.YuWebSocketHandler;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;

/**
 * 向用户推送消息的工具类
 *
 * @author yuzhihao
 * @since 2025-02-10 15:40:39
 */
@Log4j2
public class WebsocketUtil {

    /**
     * 给单个用户推送消息
     *
     * @param userId
     * @param message
     */
    public static void sendMessage(Long userId, Object message) {
        try {
            if (Objects.isNull(userId)) {
                log.error("向ID为（{}）的用户推送了消息是失败！未获取到当前用户连接信息！失败消息：{}", userId, JSONObject.toJSONString(message));
                return;
            }

            if (YuWebSocketHandler.exist(userId.toString(),"L")) {
                log.info("向ID为（{}）的用户推送了消息是：{}", userId, JSONObject.toJSONString(message));
                YuWebSocketHandler.push(userId.toString(), JSONObject.toJSONString(message));
            }else if(YuWebSocketHandler.exist(userId.toString(),"R")){
                RedisPubSubSender.send(new RedisWebsocketMessage(userId.toString(),JSONObject.from(message)));
            }

        } catch (Exception e) {
            log.error("socket 推送{}异常： {}   ,{}",userId,message,e);
        }
    }

    

    /**
     * 给单个用户推送消息
     *
     * @param message
     */
    public static void sendMessage(RedisWebsocketMessage message) {
        try {

            if (YuWebSocketHandler.exist(message.getKey())) {
                log.info("向ID为（{}）的用户推送了消息是：{}", message.getKey(), JSONObject.toJSONString(message.getMessage()));
                YuWebSocketHandler.push(message.getKey(), JSONObject.toJSONString(message.getMessage()));
            }

        } catch (Exception e) {
            log.error("socket 推送{}异常： {}   ,{}",message.getKey(),message.getMessage(),e);
        }
    }

    /**
     * 向所有在线人发送消息
     *
     * @param message
     */
    public static void sendMessageForAll(Object message) {
        YuWebSocketHandler.push(null, JSONObject.toJSONString(message));
        //jdk8 新方法
        //ONLINE_SESSION.forEach((sessionId, session) -> sendMessage(session,JSONObject.toJSONString(message)));
    }


}
