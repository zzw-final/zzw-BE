package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Chat.ChatMessage;
import com.zzw.zzw_final.Dto.Chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatRoom(ChatRoom chatRoom);
}
