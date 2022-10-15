package com.zzw.zzw_final.Dto.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatRoomOut {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JoinColumn(name = "chatRoom_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "chatMessage_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatMessage chatMessage;

    public ChatRoomOut(Member member, ChatRoom chatRoom, ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
        this.member = member;
        this.chatRoom = chatRoom;
    }

    public void update(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
}
