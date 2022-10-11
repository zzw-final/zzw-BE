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

    @PostMapping("/api/grade/{grade_id}/{user_id}")
    public ResponseDto<?> postGrade(HttpServletRequest request, @PathVariable Long grade_id,
                                    @PathVariable Long user_id){
        return gradeService.postGrade(request, grade_id, user_id);
    }
    @GetMapping("/api/member/grade")
    public ResponseDto<?> getMemberGrade(HttpServletRequest request){
        return gradeService.getMemberGrade(request);
    }

    @PutMapping("/api/member/grade/{gradeId}")
    public ResponseDto<?> updateMemberGrade(HttpServletRequest request, @PathVariable Long gradeId){
        return gradeService.updateMemberGrade(request, gradeId);
    }
}
