package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.ChatMessage;
import com.zzw.zzw_final.Dto.Entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageResponseDto {
    private String sender;
    private String message;
    private String sendTime;

    public ChatMessageResponseDto(Member getMember, ChatMessage chatMessage) {
        this.sender = getMember.getNickname();
        this.message = chatMessage.getMessage();
        this.sendTime = chatMessage.getSendTime();
    }

    public ChatMessageResponseDto(Member member, String time) {
        this.sender = member.getNickname();
        this.message = member.getNickname() + "님이 입장하셨습니다.";
        this.sendTime = time;
    }

    public ChatMessageResponseDto(Member member, String time, String message) {
        this.sender = member.getNickname();
        this.message = message;
        this.sendTime = time;
    }
}