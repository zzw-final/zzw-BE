package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.ChatMember;
import com.zzw.zzw_final.Dto.Entity.ChatMessage;
import com.zzw.zzw_final.Dto.Entity.ChatRoom;
import lombok.Getter;

@Getter
public class ChatListResponseDto {
    private Long userId;
    private String nickname;
    private String grade;
    private String profile;
    private String message;
    private String chatTime;
    private Long roomId;
    private Boolean isRead;

    public ChatListResponseDto(ChatRoom chatRoom, ChatMember chatToMember,
                               ChatMessage chatMessage, Boolean isRead) {
        this.userId = chatToMember.getMember().getId();
        this.nickname = chatToMember.getMember().getNickname();
        this.grade = chatToMember.getMember().getGrade();
        this.profile = chatToMember.getMember().getProfile();
        this.message = chatMessage.getMessage();
        this.roomId = chatRoom.getId();
        this.isRead = isRead;
        this.chatTime = chatMessage.getSendTime();
    }

    public ChatListResponseDto(ChatRoom chatRoom,
                               ChatMessage chatMessage, Boolean isRead) {
        this.nickname = "알 수 없음";
        this.profile = "https://zzwimage.s3.ap-northeast-2.amazonaws.com/zzw.-removebg-preview.png";
        this.message = chatMessage.getMessage();
        this.roomId = chatRoom.getId();
        this.isRead = isRead;
        this.chatTime = chatMessage.getSendTime();
    }
}
