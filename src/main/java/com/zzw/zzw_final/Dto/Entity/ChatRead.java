package com.zzw.zzw_final.Dto.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChatRead {

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

    public ChatRead(ChatRoom chatRoom, ChatMessage chatMessage, Member member) {
        this.chatRoom = chatRoom;
        this.member = member;
        this.chatMessage = chatMessage;
    }

    public void update(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
}
