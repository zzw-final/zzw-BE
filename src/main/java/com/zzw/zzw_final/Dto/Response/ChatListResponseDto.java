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
    }
}
