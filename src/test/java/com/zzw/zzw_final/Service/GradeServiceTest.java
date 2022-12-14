package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Dto.Entity.Grade;
import com.zzw.zzw_final.Dto.Entity.GradeList;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Entity.ProfileList;
import com.zzw.zzw_final.Dto.Request.SignupRequestDto;
import com.zzw.zzw_final.Dto.Response.GradeListResponseDto;
import com.zzw.zzw_final.Repository.GradeListRepository;
import com.zzw.zzw_final.Repository.GradeRepository;
import com.zzw.zzw_final.Repository.MemberRepository;
import com.zzw.zzw_final.Repository.ProfileListRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GradeServiceTest {
    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private GradeListRepository gradeListRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProfileListRepository profileListRepository;

    Member member;
    SignupRequestDto signupRequestDto;

    @BeforeEach
    public void setup() {
        signupRequestDto = new SignupRequestDto("요리왕", "zzw@naver.com", "kakao");
        ProfileList profileList = profileListRepository.findProfileListById(1L);
        member = new Member(signupRequestDto, profileList);
        memberRepository.save(member);

        GradeList gradeList = gradeListRepository.findGradeListById(5005L);
        GradeList gradeList2 = gradeListRepository.findGradeListById(5004L);
        GradeList gradeList3 = gradeListRepository.findGradeListById(5006L);
        gradeListRepository.save(gradeList);
        gradeListRepository.save(gradeList2);
        gradeListRepository.save(gradeList3);

        Grade grade = new Grade(member, gradeList);
        Grade grade2 = new Grade(member, gradeList2);
        Grade grade3 = new Grade(member, gradeList3);
        gradeRepository.save(grade);
        gradeRepository.save(grade2);
        gradeRepository.save(grade3);
    }

    @Test
    public void getMemberGrade() {

        //when
        List<Grade> grades = gradeRepository.findAllByMember(member);
        List<GradeListResponseDto> gradeListResponseDtos = new ArrayList<>();

        for(Grade grade : grades){
            gradeListResponseDtos.add(new GradeListResponseDto(grade.getGradeList()));
        }

        //then
        Assertions.assertEquals(member.getOauth(), "kakao");
        Assertions.assertEquals(member.getEmail(), "zzw@naver.com");
        Assertions.assertEquals(member.getNickname(), "요리왕");
        Assertions.assertEquals(grades.size(), 3);
        Assertions.assertEquals(grades.get(0).getGradeList().getName(), "먹잘알");
        Assertions.assertEquals(grades.get(1).getGradeList().getName(), "된장찌개신");
        Assertions.assertEquals(grades.get(2).getGradeList().getName(), "비건요리왕");
        Assertions.assertEquals(gradeListResponseDtos.size(), 3);
        Assertions.assertEquals(gradeListResponseDtos.get(0).getGradeName(), "먹잘알");
        Assertions.assertEquals(gradeListResponseDtos.get(1).getGradeName(), "된장찌개신");
        Assertions.assertEquals(gradeListResponseDtos.get(2).getGradeName(), "비건요리왕");
        Assertions.assertEquals(gradeListResponseDtos.get(0).getGradeId(), grades.get(0).getGradeList().getId());
        Assertions.assertEquals(gradeListResponseDtos.get(1).getGradeId(), grades.get(1).getGradeList().getId());
        Assertions.assertEquals(gradeListResponseDtos.get(2).getGradeId(), grades.get(2).getGradeList().getId());
    }

    @Test
    void updateMemberGrade() {
        //when
        GradeList gradeList = gradeListRepository.findGradeListById(63L);
        member.updateGrade(gradeList.getName());
        memberRepository.save(member);

        //then
        Assertions.assertEquals(member.getOauth(), "kakao");
        Assertions.assertEquals(member.getEmail(), "zzw@naver.com");
        Assertions.assertEquals(member.getNickname(), "요리왕");
        Assertions.assertEquals(member.getGrade(), "태초의 존재");
    }

}