package com.zzw.zzw_final.Dto.Chat;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// 처음으로 입력받는 메세지
@Getter
@Setter
@Data
public class ChatRequestDto {
    //    private MessageType type; // 메시지 타입
    private Long roomId;
    private String message;
}