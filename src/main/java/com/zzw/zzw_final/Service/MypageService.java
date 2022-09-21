package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Dto.Entity.Grade;
import com.zzw.zzw_final.Dto.Entity.GradeList;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Repository.GradeListRepository;
import com.zzw.zzw_final.Repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final MemberService memberService;
    private final GradeListRepository gradeListRepository;
    private final GradeRepository gradeRepository;
    public ResponseDto<?> getUserInfo(HttpServletRequest request) {
        return ResponseDto.success("test");
    }


    public ResponseDto<?> postGrade(HttpServletRequest request, Long grade_id) {

        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        GradeList gradeList = gradeListRepository.findGradeListById(grade_id);
        Grade userGrade = new Grade(member, gradeList);

        gradeRepository.save(userGrade);
        return ResponseDto.success("post grade success !");
    }
}
