package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Entity.ProfileList;
import com.zzw.zzw_final.Dto.Response.ProfileResponseDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Repository.MemberRepository;
import com.zzw.zzw_final.Repository.ProfileListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static com.zzw.zzw_final.Dto.ErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileListRepository profileListRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public ResponseDto<?> updateMemberProfile(HttpServletRequest request, Long profileId) {
        Member loginMember = memberService.getMember(request);
        if (loginMember == null){
            return ResponseDto.fail(MEMBER_NOT_FOUND);
        }

        ProfileList profileList = profileListRepository.findProfileListById(profileId);

        loginMember.updateProfile(profileList.getProfile());
        memberRepository.save(loginMember);
        return ResponseDto.success("success update profile");
    }

    public ResponseDto<?> getMemberProfile() {
        List<ProfileList> profileLists = profileListRepository.findAll();
        List<ProfileResponseDto> profileResponseDtos = new ArrayList<>();
        for(ProfileList profileList : profileLists){
            profileResponseDtos.add(new ProfileResponseDto(profileList));
        }
        return ResponseDto.success(profileResponseDtos);
    }

}
