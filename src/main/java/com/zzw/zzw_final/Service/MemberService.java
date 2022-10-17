package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.*;
import com.zzw.zzw_final.Dto.Request.SignupRequestDto;
import com.zzw.zzw_final.Dto.Response.*;
import com.zzw.zzw_final.Dto.TokenDto;
import com.zzw.zzw_final.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.zzw.zzw_final.Dto.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FollowRepository followRepository;
    private final GradeRepository gradeRepository;
    private final GradeListRepository gradeListRepository;
    private final PostRepository postRepository;
    private final TagListRepository tagListRepository;

    public ResponseDto<?> checkMember(HttpServletRequest request){

        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail(NULL_TOKEN);
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail(NULL_TOKEN);
        }

        if(!tokenProvider.validateToken(request.getHeader("Refresh-Token")))
            return ResponseDto.fail(INVALID_TOKEN);

        if(null == request.getHeader("oauth")){
            return ResponseDto.fail(NULL_OAUTH);
        }

        String email = tokenProvider.getUserEmail(request.getHeader("Authorization").substring(7));
        Member member = memberRepository.findMemberByEmailAndOauth(email, request.getHeader("oauth"));

        return ResponseDto.success(member);
    }

    public ResponseDto<?> postUserNickname(HttpServletResponse response, SignupRequestDto requestDto) {

        Optional<Member> nickname = memberRepository.findByNickname(requestDto.getNickname());
        if(nickname.isPresent()){
            return ResponseDto.fail(DUPLICATE_NICKNAME) ;
        }

        Member member = new Member(requestDto);
        memberRepository.save(member);

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        response.addHeader("Authorization", tokenDto.getAccessToken());
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        IntegrationResponseDto responseDto = new IntegrationResponseDto(member, tokenDto, getInvalidToken());

        GradeList gradeList = gradeListRepository.findGradeListById(5012L);
        Grade grade = new Grade(member, gradeList);
        gradeRepository.save(grade);

        //선착순 20명 repository
        if(memberRepository.findAll().size() <= 20){
            if(isMemberGetGrade(5011L, member)) {
                return ResponseDto.success(responseDto);
            }
        }
        return ResponseDto.success(responseDto);
    }

    public Member getMember(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String oauth = request.getHeader("oauth");
        if (token != null){
            String email = tokenProvider.getUserEmail(token.substring(7));
            Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);
            return member;
        }

        return null;
    }

    public ResponseDto<?> resignMember(Long member_id) {
        Member member = memberRepository.findMemberById(member_id);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByMember(member);
        if (refreshToken != null){
            refreshTokenRepository.delete(refreshToken.get());
        }
        List<Follow> followList = followRepository.findAllByFollowerId(member_id);
        for (Follow follow : followList){
            followRepository.delete(follow);
        }
        memberRepository.delete(member);

        return ResponseDto.success("success member delete!");
    }


    public String getInvalidToken(){
        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.DATE, 1);
        Date date = new Date(cal1.getTimeInMillis());
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return transFormat.format(date);
    }

    public ResponseDto<?> getUserInfo(HttpServletRequest request) {

        ResponseDto<?> result = checkMember(request);
        Member member = (Member) result.getData();

        List<Follow> followerlist = followRepository.findAllByFollowerId(member.getId());
        List<Follow> followList = followRepository.findAllByMember(member);

        List<GradeListResponseDto> responseDtos = getUserGrade(member);
        List<Post> posts = postRepository.findAllByMember(member);

        MypageUserInfoResponseDto responseDto = new MypageUserInfoResponseDto(member,
                followerlist.size(), followList.size(), responseDtos, true, posts.size());

        return ResponseDto.success(responseDto);
    }



    public ResponseDto<?> getOtherUserInfo(Long user_id, HttpServletRequest request) {

        Member loginMember = getMember(request);
        Member member = memberRepository.findMemberById(user_id);

        List<Follow> followerlist = followRepository.findAllByFollowerId(member.getId());
        List<Follow> followList = followRepository.findAllByMember(member);

        List<GradeListResponseDto> responseDtos = getUserGrade(member);
        List<Post> posts = postRepository.findAllByMember(member);


        MypageUserInfoResponseDto responseDto;

        if(loginMember == null){
            responseDto = new MypageUserInfoResponseDto(member,
                    followerlist.size(), followList.size(), responseDtos, false, posts.size());

            return ResponseDto.success(responseDto);
        }
        else
            responseDto = getUserInfoResponseDto(loginMember, member, followerlist, followList, responseDtos, posts.size());

        return ResponseDto.success(responseDto);
    }

    private MypageUserInfoResponseDto getUserInfoResponseDto(Member loginMember, Member member, List<Follow> followerlist,
                                                             List<Follow> followList, List<GradeListResponseDto> responseDtos, int postSize) {
        Follow follow = followRepository.findFollowByFollowerIdAndMember(loginMember.getId(), member);

        if (follow == null) {
            MypageUserInfoResponseDto responseDto = new MypageUserInfoResponseDto(member,
                    followerlist.size(), followList.size(), responseDtos,false, postSize);
            return responseDto;

        } else {
            MypageUserInfoResponseDto responseDto = new MypageUserInfoResponseDto(member,
                    followerlist.size(), followList.size(), responseDtos,true, postSize);
            return responseDto;
        }
    }

    public List<GradeListResponseDto> getUserGrade(Member member) {
        List<GradeListResponseDto> gradeListResponseDtos = new ArrayList<>();

        List<Grade> grades = gradeRepository.findAllByMember(member);
        for(Grade grade : grades){
            gradeListResponseDtos.add(new GradeListResponseDto(grade.getGradeList()));
        }
        return gradeListResponseDtos;
    }

    public Boolean isGetGrade(Member member, Post post){

        Boolean isGet = memberPostSizeCheck(member);

        List<TagList> tagLists = tagListRepository.findAllByPost(post);
        for (TagList tagList : tagLists){
            if (tagList.getName().contains("김치")){
                if(isMemberGetGrade(5007L, member))
                    isGet = true;
            } else if (tagList.getName().contains("된장찌개")) {
                if(isMemberGetGrade(5004L, member))
                    isGet = true;
            }
        }

        List<TagList> allTagList = tagListRepository.findAll();
        int tagNum = 0;
        for(TagList tagList: allTagList){
            if(tagList.getPost().getMember() == member)
                tagNum++;
            if(tagNum > 30)
                if (isMemberGetGrade(5006L, member))
                    isGet = true;
        }
        return isGet;
    }

    public Boolean memberPostSizeCheck(Member member){
        Boolean isGetGrade = false;
        Long gradeListId = 1L;

        List<Post> posts = postRepository.findAllByMember(member);

        switch (posts.size()){
            case 5:
                gradeListId = 5005L;
                break;
            case 10:
                gradeListId = 5003L;
                break;
            case 20:
                gradeListId = 5002L;
        }

        if (gradeListId != 1L) {
            if(isMemberGetGrade(5005L, member))
                isGetGrade = true;
        }
        return isGetGrade;
    }
    
    public Boolean isMemberGetGrade(Long gradeListId, Member member){
        GradeList gradeList = gradeListRepository.findGradeListById(gradeListId);
        Grade grade = gradeRepository.findGradeByMemberAndGradeList(member, gradeList);
        if (grade == null) {
            Grade getGrade = new Grade(member, gradeList);
            gradeRepository.save(getGrade);
            return true;
        }
        return false;
    }
}
