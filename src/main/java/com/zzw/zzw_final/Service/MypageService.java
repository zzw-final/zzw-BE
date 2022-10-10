package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Dto.Entity.*;
import com.zzw.zzw_final.Dto.Response.*;
import com.zzw.zzw_final.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final MemberService memberService;
    private final GradeListRepository gradeListRepository;
    private final GradeRepository gradeRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;


    public ResponseDto<?> getUserInfo(HttpServletRequest request) {

        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        List<Follow> followerlist = followRepository.findAllByFollowerId(member.getId());
        List<Follow> followList = followRepository.findAllByMember(member);

        List<GradeListResponseDto> responseDtos = getUserGrade(member);

        MypageUserInfoResponseDto responseDto = new MypageUserInfoResponseDto(member,
                followerlist.size(), followList.size(), responseDtos, true);

        return ResponseDto.success(responseDto);
    }


    public ResponseDto<?> postGrade(HttpServletRequest request, Long grade_id, Long user_id) {

        Member member = memberRepository.findMemberById(user_id);

        GradeList gradeList = gradeListRepository.findGradeListById(grade_id);
        Grade userGrade = new Grade(member, gradeList);

        gradeRepository.save(userGrade);
        return ResponseDto.success("post grade success !");
    }

    public ResponseDto<?> getUserPost(HttpServletRequest request) {
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        List<Post> posts = postRepository.findAllByMember(member);
        List<PostResponseDto> userPostResponseDtos = new ArrayList<>();

        for (Post post : posts) {
            List<IngredientResponseDto> ingredientResponseDtos = postService.getIngredientByPost(post);
            userPostResponseDtos.add(postService.getResponsePostUserLike(member, post, ingredientResponseDtos));
        }
        return ResponseDto.success(userPostResponseDtos);
    }

    public ResponseDto<?> getUserLikePosts(HttpServletRequest request) {
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        List<PostLike> postLikes = postLikeRepository.findAllByMember(member);
        List<Post> posts = new ArrayList<>();

        for (PostLike postLike : postLikes) {
            posts.add(postRepository.findPostById(postLike.getPost().getId()));
        }

        List<PostResponseDto> userPostResponseDtos = new ArrayList<>();

        for (Post post : posts) {
            List<IngredientResponseDto> ingredientResponseDtos = postService.getIngredientByPost(post);
            userPostResponseDtos.add(postService.getResponsePostUserLike(member, post, ingredientResponseDtos));
        }

        return ResponseDto.success(userPostResponseDtos);
    }


    public ResponseDto<?> getOtherUserInfo(Long user_id, HttpServletRequest request) {

        Member loginMember = memberService.getMember(request);
        Member member = memberRepository.findMemberById(user_id);

        List<Follow> followerlist = followRepository.findAllByFollowerId(member.getId());
        List<Follow> followList = followRepository.findAllByMember(member);

        List<GradeListResponseDto> responseDtos = getUserGrade(member);


        MypageUserInfoResponseDto responseDto;

        if(loginMember == null){
            responseDto = new MypageUserInfoResponseDto(member,
                    followerlist.size(), followList.size(), responseDtos, false);

            return ResponseDto.success(responseDto);
        }
        else
            responseDto = getUserInfoResponseDto(loginMember, member, followerlist, followList, responseDtos);

        return ResponseDto.success(responseDto);
    }

    private MypageUserInfoResponseDto getUserInfoResponseDto(Member loginMember, Member member, List<Follow> followerlist,
                                                             List<Follow> followList, List<GradeListResponseDto> responseDtos) {
        Follow follow = followRepository.findFollowByFollowerIdAndMember(loginMember.getId(), member);

        if (follow == null) {
            MypageUserInfoResponseDto responseDto = new MypageUserInfoResponseDto(member,
                    followerlist.size(), followList.size(), responseDtos,false);
            return responseDto;

        } else {
            MypageUserInfoResponseDto responseDto = new MypageUserInfoResponseDto(member,
                    followerlist.size(), followList.size(), responseDtos,true);
            return responseDto;
        }
    }

    private List<GradeListResponseDto> getUserGrade(Member member) {
        List<GradeListResponseDto> gradeListResponseDtos = new ArrayList<>();

        List<Grade> grades = gradeRepository.findAllByMember(member);
        for(Grade grade : grades){
            gradeListResponseDtos.add(new GradeListResponseDto(grade.getGradeList()));
        }
        return gradeListResponseDtos;
    }

    public ResponseDto<?> getOtherUserPosts(Long user_id, HttpServletRequest request) {
        Member loginMember = memberService.getMember(request);

        Member postMember = memberRepository.findMemberById(user_id);

        List<Post> posts = postRepository.findAllByMember(postMember);
        List<PostResponseDto> userPostResponseDtos = new ArrayList<>();

        for (Post post : posts) {
            List<IngredientResponseDto> ingredientResponseDtos = postService.getIngredientByPost(post);
            userPostResponseDtos.add(postService.getResponsePostUserLike(loginMember, post, ingredientResponseDtos));
        }
        return ResponseDto.success(userPostResponseDtos);
    }

}

