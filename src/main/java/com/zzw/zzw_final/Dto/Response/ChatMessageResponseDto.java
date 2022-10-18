package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.ChatMessage;
import com.zzw.zzw_final.Dto.Entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageResponseDto {
    private String sender;
    private Long messageId;
    private String profile;
    private String message;
    private String sendTime;

    public ChatMessageResponseDto(Member getMember, ChatMessage chatMessage) {
        this.sender = (getMember!= null) ? getMember.getNickname() : "알 수 없음";
        this.profile = (getMember!= null) ? getMember.getProfile() : "https://zzwimage.s3.ap-northeast-2.amazonaws.com/zzw.-removebg-preview.png";
        this.message = chatMessage.getMessage();
        this.sendTime = chatMessage.getSendTime();
        this.messageId = chatMessage.getId();
    }

    public ChatMessageResponseDto(Member member, String time) {
        this.sender = member.getNickname();
        this.message = member.getNickname() + "님이 입장하셨습니다.";
        this.sendTime = time;
    }

    public ChatMessageResponseDto(Member member, String time, String message, Long id) {
        this.sender = member.getNickname();
        this.profile = member.getProfile();
        this.message = message;
        this.sendTime = time;
        this.messageId = id;
    }
}