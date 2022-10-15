package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.ChatRoom;
import com.zzw.zzw_final.Dto.Entity.ChatRoomOut;
import com.zzw.zzw_final.Dto.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomOutRepository extends JpaRepository<ChatRoomOut, Long> {
    ChatRoomOut findChatRoomOutByMemberAndChatRoom(Member member, ChatRoom chatRoom);
}
