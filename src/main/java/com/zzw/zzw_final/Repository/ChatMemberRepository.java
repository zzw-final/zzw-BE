package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.ChatMember;
import com.zzw.zzw_final.Dto.Entity.ChatRoom;
import com.zzw.zzw_final.Dto.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
    ChatMember findChatMemberByChatRoomAndMember(ChatRoom chatRoom, Member member);
    ChatMember findChatMemberByChatRoomAndMemberNot(ChatRoom chatRoom, Member member);
    List<ChatMember> findAllByMember(Member member);
}
