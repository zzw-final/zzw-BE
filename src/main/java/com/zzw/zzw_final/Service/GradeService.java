package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Dto.Entity.Grade;
import com.zzw.zzw_final.Dto.Entity.GradeList;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Response.GradeListResponseDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Repository.GradeListRepository;
import com.zzw.zzw_final.Repository.GradeRepository;
import com.zzw.zzw_final.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.zzw.zzw_final.Dto.ErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final MemberService memberService;
    private final GradeRepository gradeRepository;

    public ResponseDto<?> getMemberGrade(HttpServletRequest request) {
        Member loginMember = memberService.getMember(request);
        if (loginMember == null){
            return ResponseDto.fail(MEMBER_NOT_FOUND);
        }

        List<Grade> grades = gradeRepository.findAllByMember(loginMember);
        List<GradeListResponseDto> gradeListResponseDtos = new ArrayList<>();

        for(Grade grade : grades){
            gradeListResponseDtos.add(new GradeListResponseDto(grade.getGradeList()));
        }

        return ResponseDto.success(gradeListResponseDtos);
    }
}
