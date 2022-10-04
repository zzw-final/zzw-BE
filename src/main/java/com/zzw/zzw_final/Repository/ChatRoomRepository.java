package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByRoomId(String roomId);
}
