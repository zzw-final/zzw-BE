package com.zzw.zzw_final.Dto.Request;

import lombok.Getter;

@Getter
public class CheckReadMessageRequestDto {
    private Long roomId;
    private Long messageId;
}
