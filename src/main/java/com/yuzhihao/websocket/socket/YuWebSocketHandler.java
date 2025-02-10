package com.yuzhihao.websocket.socket;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * 设备推送websocket
 *
 * @author yuzhihao
 */
@Log4j2
@Component
@AllArgsConstructor
public class YuWebSocketHandler implements org.springframework.web.socket.WebSocketHandler {

    private static StringRedisTemplate REDIS_CACHE_S;

    private final StringRedisTemplate redisCache;

    /**
     * 保存用户会话信息，用于服务端群发
     */
    private static final Map<String, List<WebSocketSession>> MAP_SESSION = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        REDIS_CACHE_S = redisCache;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        String uni = (String) session.getAttributes().get("uni_id");
        if (StringUtils.hasLength(uni)) {
            log.info("websocket add  id ：{}，session：{},", uni, session.getId());
            MAP_SESSION.computeIfAbsent(uni, (e) -> new LinkedList<>()).add(session);
            redisCache.opsForValue().set(uni, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 3 * 24 * 60 * 60, TimeUnit.SECONDS);
        } else {
            session.close();
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        String uni = (String) session.getAttributes().get("uni_id");
        log.info("websocket id:{}, handle message : {}", session.getId(), message.toString());
        if (session.isOpen()) {
            session.sendMessage(new TextMessage("连接成功"));
        }
        //有通信则刷新缓存
        redisCache.opsForValue().set(uni, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 3 * 24 * 60 * 60, TimeUnit.SECONDS);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("websocket transport error id:{}", session.getId(), exception.getCause());
        session.close();
        this.removeSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.warn("websocket connection close id:{} , status:{}", session.getId(), closeStatus.toString());
        this.removeSession(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 移除掉session
     *
     * @param session
     */
    private void removeSession(WebSocketSession session) {
        String uni = (String) session.getAttributes().get("uni_id");
        if (StringUtils.hasLength(uni)) {
            Optional.ofNullable(MAP_SESSION.get(uni)).ifPresent(list -> list.removeIf(e -> e.getId().equals(session.getId())));
        } else {
            MAP_SESSION.values().forEach(list -> list.removeIf(e -> e.getId().equals(session.getId())));
        }
        redisCache.delete(uni);
        MAP_SESSION.entrySet().removeIf(e -> e.getValue().isEmpty());
    }

    public static boolean exist(String key, Object... args) {
        if (args.length > 0) {
            if ("L".equals(args[0])) {
                return MAP_SESSION.containsKey(key);
            }
            if ("R".equals(args[0])) {
                return Boolean.TRUE.equals(REDIS_CACHE_S.hasKey(key));
            }
        }
        return MAP_SESSION.containsKey(key);
    }

    public static void push(String key, String result) {
        if (StringUtils.hasLength(key)) {
            Optional.ofNullable(MAP_SESSION.get(key)).ifPresent(list -> {
                list.forEach(session -> {
                    try {
                        if (session.isOpen()) {
                            session.sendMessage(new TextMessage(result));
                        }
                    } catch (Exception e) {
                        log.error("websocket push error :{}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                });
            });
        } else {
            MAP_SESSION.values().forEach(list -> list.forEach(session -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(result));
                    }
                } catch (Exception e) {
                    log.error("all websocket push error ", e);
                }
            }));
        }
    }

}


