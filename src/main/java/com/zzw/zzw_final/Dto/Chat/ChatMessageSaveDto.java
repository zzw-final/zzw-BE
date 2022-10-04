package com.zzw.zzw_final.Dto.Chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageSaveDto{
    private String roomId;
    private String writer;
    private String message;

}