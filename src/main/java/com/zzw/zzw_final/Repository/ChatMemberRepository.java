package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Chat.ChatMember;
import com.zzw.zzw_final.Dto.Chat.ChatRoom;
import com.zzw.zzw_final.Dto.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
    ChatMember findChatMemberByChatRoomAndMember(ChatRoom chatRoom, Member member);
}
