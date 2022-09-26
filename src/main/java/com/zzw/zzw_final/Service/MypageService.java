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
    private final ContentRepository contentRepository;
    private final PostService postService;
    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;

    public ResponseDto<?> getUserInfo(HttpServletRequest request) {

        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        List<Follow> followerlist = followRepository.findAllByFollowerId(member.getId());
        List<Follow> followList = followRepository.findAllByMember(member);

        List<GradeListResponseDto> responseDtos = new ArrayList<>();
        List<Grade> grades = gradeRepository.findAllByMember(member);
        for(Grade grade : grades){
            responseDtos.add(new GradeListResponseDto(grade.getGradeList()));
        }

        MypageUserInfoResponseDto responseDto = new MypageUserInfoResponseDto(member,
                followList.size(), followerlist.size(), responseDtos);

        return ResponseDto.success(responseDto);
    }


    public ResponseDto<?> postGrade(HttpServletRequest request, Long grade_id, Long user_id) {

//        ResponseDto<?> result = memberService.checkMember(request);
//        Member member = (Member) result.getData();

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

        for(Post post : posts){
            Content content = contentRepository.findContentByPost(post);
            List<IngredientResponseDto> ingredientResponseDtos = postService.getIngredientByPost(post);
            userPostResponseDtos.add(postService.getResponsePostUserLike(member, post, content, ingredientResponseDtos));
        }
        return ResponseDto.success(userPostResponseDtos);
    }

    public ResponseDto<?> getUserLikePosts(HttpServletRequest request) {
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        List<PostLike> postLikes = postLikeRepository.findAllByMember(member);
        List<Post> posts = new ArrayList<>();

        for (PostLike postLike : postLikes){
            posts.add(postRepository.findPostById(postLike.getPost().getId()));
        }

        List<PostResponseDto> userPostResponseDtos = new ArrayList<>();

        for(Post post : posts){
            Content content = contentRepository.findContentByPost(post);
            List<IngredientResponseDto> ingredientResponseDtos = postService.getIngredientByPost(post);
            userPostResponseDtos.add(postService.getResponsePostUserLike(member, post, content, ingredientResponseDtos));
        }

        return ResponseDto.success(userPostResponseDtos);
    }

    public ResponseDto<?> getOtherUserInfo(Long user_id) {
        Member member = memberRepository.findMemberById(user_id);

        List<Follow> followerlist = followRepository.findAllByFollowerId(member.getId());
        List<Follow> followList = followRepository.findAllByMember(member);

        List<GradeListResponseDto> responseDtos = new ArrayList<>();
        List<Grade> grades = gradeRepository.findAllByMember(member);
        for(Grade grade : grades){
            responseDtos.add(new GradeListResponseDto(grade.getGradeList()));
        }

        MypageUserInfoResponseDto responseDto = new MypageUserInfoResponseDto(member,
                followList.size(), followerlist.size(), responseDtos);

        return ResponseDto.success(responseDto);
    }

    public ResponseDto<?> getOtherUserPosts(Long user_id, HttpServletRequest request) {
        Member loginMember = memberService.getMember(request);

        Member postMember = memberRepository.findMemberById(user_id);

        List<Post> posts = postRepository.findAllByMember(postMember);
        List<PostResponseDto> userPostResponseDtos = new ArrayList<>();

        for(Post post : posts){
            Content content = contentRepository.findContentByPost(post);
            List<IngredientResponseDto> ingredientResponseDtos = postService.getIngredientByPost(post);
            userPostResponseDtos.add(postService.getResponsePostUserLike(loginMember, post, content, ingredientResponseDtos));
        }
        return ResponseDto.success(userPostResponseDtos);
    }
}
