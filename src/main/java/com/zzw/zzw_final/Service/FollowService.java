package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Dto.Entity.Follow;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Response.FollowResponseDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Repository.FollowRepository;
import com.zzw.zzw_final.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import static com.zzw.zzw_final.Dto.ErrorCode.SAME_PERSON;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;


    public ResponseDto<?> follow(HttpServletRequest request, Long member_id) {

        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        Member followMember = memberRepository.findMemberById(member_id);
        if(member.getId() == followMember.getId()){
            return ResponseDto.fail(SAME_PERSON);              //예외처리
        }

        Follow follow = followRepository.findFollowByFollowerIdAndMember(member.getId(), followMember);

        if (follow == null) {
            Follow followUser = new Follow(member, followMember);
            followRepository.save(followUser);
            return ResponseDto.success("follow success");

        } else {
            followRepository.delete(follow);

            return ResponseDto.success("unfollow success");
        }
    }

    public ResponseDto<?> getFollow(HttpServletRequest request) {

        ResponseDto<?> result = memberService.checkMember(request);
        Member loginMember = (Member) result.getData();

        List<Member> members = getFollowMembers(loginMember);
        List<FollowResponseDto> followResponseDtos = new ArrayList<>();

        for(Member member : members){
            followResponseDtos.add(getFollowerResponseDto(loginMember, member));
        }

        return ResponseDto.success(followResponseDtos);
    }

    public ResponseDto<?> getFollower(HttpServletRequest request) {

        ResponseDto<?> result = memberService.checkMember(request);
        Member LoginMember = (Member) result.getData();

        List<Member> members = getFollowMembers(LoginMember);
        List<FollowResponseDto> followerResponseDtos = new ArrayList<>();

        for (Member member : members) {
            followerResponseDtos.add(getFollowerResponseDto(LoginMember, member));
        }
        return ResponseDto.success(followerResponseDtos);
    }

    private List<Member> getFollowMembers(Member loginMember) {
        List<Follow> followerlist = followRepository.findAllByMemberOrderByFollowerNicknameAsc(loginMember);

        List<Member> members = new ArrayList<>();

        for (Follow follower : followerlist) {
            Member member2 = memberRepository.findMemberById(follower.getFollowerId());
            members.add(member2);
        }
        return members;
    }

    private FollowResponseDto getFollowerResponseDto(Member loginMember, Member member) {
        Follow follow = followRepository.findFollowByFollowerIdAndMember(loginMember.getId(), member);
        if (follow == null) {
            return new FollowResponseDto(member);
        } else {
            return new FollowResponseDto(member, true);
        }
    }

    public ResponseDto<?> getOthersFollow (Long user_id, HttpServletRequest request){

        Member loginMember = memberService.getMember(request);

        Member member = memberRepository.findMemberById(user_id);

        List<Member> members = getFollowMembers(member);
        List<FollowResponseDto> followResponseDtos = new ArrayList<>();

        if (loginMember == null){
            for (Member member1 : members) {
                followResponseDtos.add(new FollowResponseDto(member1));
            }
        }
        else{
            for (Member member1 : members) {
                followResponseDtos.add(getFollowerResponseDto(loginMember, member1));
            }
        }

        return ResponseDto.success(followResponseDtos);
    }

    public ResponseDto<?> getOthersFollower(Long user_id, HttpServletRequest request) {

        Member loginMember = memberService.getMember(request);

        Member member = memberRepository.findMemberById(user_id);

        List<Member> members = getFollowMembers(member);
        List<FollowResponseDto> followerResponseDtos = new ArrayList<>();

        for (Member member2 : members) {
            if (loginMember == null)
                followerResponseDtos.add(new FollowResponseDto(member2));
            else
                followerResponseDtos.add(getFollowerResponseDto(loginMember, member2));
        }

        return ResponseDto.success(followerResponseDtos);
    }

}
