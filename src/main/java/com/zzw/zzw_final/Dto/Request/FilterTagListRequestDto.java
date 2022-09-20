package com.zzw.zzw_final.Dto.Request;

import com.zzw.zzw_final.Dto.Response.BestTagResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterTagListRequestDto {
    private List<BestTagResponseDto> tagList;
}
