package com.zzw.zzw_final.Dto.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String roomId;

    @Column
    private String roomName;

    @Column
    private String chatMentor;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessages;


    public static ChatRoom toChatRoomEntity(String roomName, String roomId, String chatMentor){
        ChatRoom chatRoomEntity = new ChatRoom();
        chatRoomEntity.setRoomName(roomName);
        chatRoomEntity.setRoomId(roomId);
        chatRoomEntity.setChatMentor(chatMentor);
        return chatRoomEntity;
    }
}