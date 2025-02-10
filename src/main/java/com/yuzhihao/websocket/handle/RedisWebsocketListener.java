package com.yuzhihao.websocket.handle;

import com.yuzhihao.websocket.RedisWebsocketMessage;
import com.yuzhihao.websocket.WebsocketUtil;
import com.yuzhihao.websocket.frame.RedisPubSubListener;
import com.yuzhihao.websocket.frame.RedisPublishSubscribeListener;
import com.yuzhihao.websocket.socket.SocketTest;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 监听不同系统收到的websocket消息
 * <p>
 * 测试结果 100000 发布十万条消息 耗时 56666 差不多57秒，处理时间一样，发布时间长。
 * 计算出差不多一秒 可以 处理1780个消息。
 *
 * @author yuzhihao
 */
@Slf4j
@Component
@RedisPublishSubscribeListener
public class RedisWebsocketListener extends RedisPubSubListener<RedisWebsocketMessage> {

    private static final AtomicInteger COUNT = new AtomicInteger(0);

    @PostConstruct
    public void performance() {
        SocketTest.EXECUTOR.scheduleAtFixedRate(() -> {
            log.info("Redis发布订阅每10分钟接受订阅消息数量：{}", COUNT);
            COUNT.set(0);
        }, 0, 10 * 60, TimeUnit.SECONDS);
    }

    @Override
    public void onMessage(RedisWebsocketMessage message) {
        COUNT.incrementAndGet();
        //把接受到的消息进行处理
        WebsocketUtil.sendMessage(message);
    }


}
