package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
@RestController
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @GetMapping("/api/member/grade")
    public ResponseDto<?> getMemberGrade(HttpServletRequest request){
        return gradeService.getMemberGrade(request);
    }

    @PutMapping("/api/member/grade/{gradeId}")
    public ResponseDto<?> updateMemberGrade(HttpServletRequest request, @PathVariable Long gradeId){
        return gradeService.updateMemberGrade(request, gradeId);
    }
}
