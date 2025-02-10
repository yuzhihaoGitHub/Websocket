package com.yuzhihao.websocket.socket;

import com.yuzhihao.websocket.WebsocketUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yuzhihao
 */
@Component
public class SocketTest {

   public final static ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(1);

   @PostConstruct
   public void init(){
       EXECUTOR.scheduleAtFixedRate(()->{

           WebsocketUtil.sendMessageForAll(String.format("hello world ! ：%s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

       },1,1, TimeUnit.SECONDS);
   }

}
