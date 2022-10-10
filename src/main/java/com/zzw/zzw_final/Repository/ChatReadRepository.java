package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.ChatRead;
import com.zzw.zzw_final.Dto.Entity.ChatRoom;
import com.zzw.zzw_final.Dto.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatReadRepository extends JpaRepository<ChatRead, Long> {
    ChatRead findChatReadByMemberAndChatRoom(Member member, ChatRoom chatRoom);
}
