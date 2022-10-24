package com.zzw.zzw_final.Dto.Entity;

import com.zzw.zzw_final.Dto.Request.ChatRequestDto;
import lombok.*;
import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class ChatMessage extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String message;

    @Column
    private Long memberId;

    @Column
    private String sendTime;

    @JoinColumn(name = "chat_room_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @OneToMany(mappedBy = "chatMessage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRead> chatReads;

    @OneToMany(mappedBy = "chatMessage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomOut> chatRoomOuts;

    public ChatMessage(Member member, ChatRoom chatRoom, ChatRequestDto message, String time) {
        this.message = message.getMessage();
        this.memberId = member.getId();
        this.chatRoom = chatRoom;
        this.sendTime = time;
    }
}