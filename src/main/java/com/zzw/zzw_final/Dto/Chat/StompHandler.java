//package com.zzw.zzw_final.Dto.Chat;
//
//import com.zzw.zzw_final.Config.Jwt.TokenProvider;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class StompHandler implements ChannelInterceptor {
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//
//        if (StompCommand.CONNECT == accessor.getCommand()) { // websocket 연결요청
////            String jwtToken = accessor.getFirstNativeHeader("token");
//            System.out.println("connect !! -서버 ");
//        }
//
//        else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청
////            String jwtToken = accessor.getFirstNativeHeader("token");
//            System.out.println("SUBSCRIBE !! - 서버 ");
//        }
//
//        else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료
//            String jwtToken = accessor.getFirstNativeHeader("token");
//            System.out.println("DISCONNECT !! : " + jwtToken);
//        }
//        return message;
//    }
//
//}