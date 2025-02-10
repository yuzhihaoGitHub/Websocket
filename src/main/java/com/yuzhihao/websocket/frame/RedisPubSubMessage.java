package com.yuzhihao.websocket.frame;

/**
 * @author yuzhihao
 */
public interface RedisPubSubMessage {
    /**
     * 消息体code
     *
     * @return code编码
     */
    default String code() {
        return this.getClass().getSimpleName();
    }

}
