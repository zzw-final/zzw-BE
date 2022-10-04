package com.zzw.zzw_final.Dto.Entity;

import com.zzw.zzw_final.Dto.Chat.ChatMessageSaveDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chatRoom_id")
    private ChatRoom chatRoom;

    private String writer;

    @Column
    private String message;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime sendDate;

    public static ChatMessage toChatEntity(ChatMessageSaveDto chatMessageSaveDTO, ChatRoom chatRoomEntity){
        ChatMessage chatMessageEntity = new ChatMessage();

        chatMessageEntity.setChatRoom(chatRoomEntity);

        chatMessageEntity.setWriter(chatMessageSaveDTO.getWriter());
        chatMessageEntity.setMessage(chatMessageSaveDTO.getMessage());

        return chatMessageEntity;

    }
}