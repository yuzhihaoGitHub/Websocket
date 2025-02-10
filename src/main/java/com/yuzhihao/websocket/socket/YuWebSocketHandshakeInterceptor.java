package com.yuzhihao.websocket.socket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * 充电WebSocket握手拦截器
 *
 * @author yuzhihao
 */
public class YuWebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;

            String path = servletRequest.getServletRequest().getServletPath();
            String[] pathSegments = path.split("/");
            String uniId = pathSegments[pathSegments.length - 1];

            if (StringUtils.hasLength(uniId)) {
                attributes.put("uni_id", uniId);
                return true;
            }
        }
        return false;
    }
}

