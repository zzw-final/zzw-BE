package com.zzw.zzw_final.Dto.Request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ChatRequestDto {
    private Long roomId;
    private String message;
}