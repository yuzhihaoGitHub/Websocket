package com.yuzhihao.websocket.config;


import com.yuzhihao.websocket.socket.YuWebSocketHandler;
import com.yuzhihao.websocket.socket.YuWebSocketHandshakeInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * 配置
 *
 * @author yuzhihao
 */
@Configuration
@EnableWebSocket
@AllArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final YuWebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        //配置handler,拦截器和跨域
        registry.addHandler(this.webSocketHandler, "/socket/{userId}")
                .addInterceptors(new YuWebSocketHandshakeInterceptor())
                .setAllowedOrigins("*");

    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        //文本消息最大缓存
        container.setMaxTextMessageBufferSize(8192);
        //二进制消息大战缓存
        container.setMaxBinaryMessageBufferSize(8192);
        // 最大闲置时间，3分钟没动自动关闭连接
        container.setMaxSessionIdleTimeout(60 * 1000L);
        //异步发送超时时间
        container.setAsyncSendTimeout(10L * 1000);
        return container;
    }


}


