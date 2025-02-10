package com.yuzhihao.websocket.config;

import com.yuzhihao.websocket.frame.RedisPubSubListener;
import com.yuzhihao.websocket.frame.RedisPubSubMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * redis消息队列配置-订阅者
 * @author 网络worker
 */
@Configuration
@Log4j2
public class RedisMessageListenerConfig  {


    /**
     * 创建连接工厂
     * @param connectionFactory
     * @param listeners
     * @return
     */
    @Bean
    public RedisMessageListenerContainer listenerContainer(RedisConnectionFactory connectionFactory,
                                                   List<RedisPubSubListener<?>> listeners) throws Exception {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        for (RedisPubSubListener<?> listener : listeners) {
            container.addMessageListener(listener,new PatternTopic(getTopic(listener.getClass())));
        }

        return container;
    }

    private static String getTopic(Class<?> originalClass) throws Exception {
        // 获取当前类的直接超类的 Type
        Type superClass = originalClass.getGenericSuperclass();

        // 检查超类是否是 ParameterizedType
        if (superClass instanceof ParameterizedType) {
            // 获取第一个泛型参数的实际类型
            Type[] actualTypeArguments = ((ParameterizedType) superClass).getActualTypeArguments();

            Class<?> rawType = null;
            Type types =  actualTypeArguments[0];

            // 返回第一个泛型参数的类类型
            if(types instanceof Class){ //一个范型
                rawType =  (Class<?>)types;
            }

            if(!RedisPubSubMessage.class.isAssignableFrom(rawType)){
                throw new Exception("消息类型必须是RedisPubSubMessage的子类");
            }

            // 获取默认构造函数
            Constructor<?> ctor = rawType.getConstructor();
            // 创建对象
            RedisPubSubMessage message = (RedisPubSubMessage) ctor.newInstance();
            // 调用方法
            return message.code();

        }

        throw new Exception("Redis消息发布订阅配置错误，监听器 RedisPublishSubscribeListener 没有配置消息体未继承 RedisPubSubMessage 没有找到泛型参数");

    }

}

