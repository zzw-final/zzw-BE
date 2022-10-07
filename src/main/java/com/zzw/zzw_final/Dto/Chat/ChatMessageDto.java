package com.zzw.zzw_final.Dto.Chat;

import com.zzw.zzw_final.Dto.Entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ChatMessageDto {
    private String sender;
    private String message;
    private String sendTime;

    public ChatMessageDto(Member getMember, ChatMessage chatMessage) {
        this.sender = getMember.getNickname();
        this.message = chatMessage.getMessage();
        this.sendTime = chatMessage.getSendTime();
    }

    public ChatMessageDto(Member member, String time) {
        this.sender = member.getNickname();
        this.message = member.getNickname() + "님이 입장하셨습니다.";
        this.sendTime = time;
    }

    public ChatMessageDto(Member member, String time, String message) {
        this.sender = member.getNickname();
        this.message = message;
        this.sendTime = time;
    }
}