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
    private final GradeListRepository gradeListRepository;
    private final MemberRepository memberRepository;
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

    public ResponseDto<?> updateMemberGrade(HttpServletRequest request, Long gradeId) {
        Member loginMember = memberService.getMember(request);
        if (loginMember == null){
            return ResponseDto.fail(MEMBER_NOT_FOUND);
        }

        GradeList gradeList = gradeListRepository.findGradeListById(gradeId);
        loginMember.updateGrade(gradeList.getName());
        memberRepository.save(loginMember);

        return ResponseDto.success("success update grade");
    }

    public ResponseDto<?> postGrade(HttpServletRequest request, Long grade_id, Long user_id) {

        Member member = memberRepository.findMemberById(user_id);

        if(member != null){
            GradeList gradeList = gradeListRepository.findGradeListById(grade_id);
            Grade userGrade = new Grade(member, gradeList);
            // 이 칭호를 이미 소유하고 있는가 ?를 판단
            if (userGrade == null){
                // 칭호를 획득하게 해준다.
            }

            gradeRepository.save(userGrade);
        }
        return ResponseDto.success("post grade success !");
    }
}
