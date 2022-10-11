package com.zzw.zzw_final.Dto.Response;


import com.zzw.zzw_final.Dto.Entity.GradeList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeListResponseDto {
    private String gradeName;
    private Long gradeId;

    public GradeListResponseDto(GradeList gradeList){
        this.gradeName = gradeList.getName();
        this.gradeId = gradeList.getId();
    }
}
