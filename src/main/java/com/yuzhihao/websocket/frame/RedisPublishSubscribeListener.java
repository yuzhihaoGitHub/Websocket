package com.yuzhihao.websocket.frame;

import java.lang.annotation.*;

/**
 * redis发布订阅监听注解
 *
 * @author cpile
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisPublishSubscribeListener {

}
