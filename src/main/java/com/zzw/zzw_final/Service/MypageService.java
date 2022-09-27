package com.zzw.zzw_final.Service;

import com.amazonaws.Response;
import com.zzw.zzw_final.Dto.Entity.*;
import com.zzw.zzw_final.Dto.Response.*;
import com.zzw.zzw_final.Repository.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.Criteria;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.zzw.zzw_final.Dto.ErrorCode.SAME_PERSON;

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
        for (Grade grade : grades) {
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

        for (Post post : posts) {
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

        for (PostLike postLike : postLikes) {
            posts.add(postRepository.findPostById(postLike.getPost().getId()));
        }

        List<PostResponseDto> userPostResponseDtos = new ArrayList<>();

        for (Post post : posts) {
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
                followerlist.size(), followList.size(), responseDtos);


        return ResponseDto.success(responseDto);
    }

    public ResponseDto<?> getOtherUserPosts(Long user_id, HttpServletRequest request) {
        Member loginMember = memberService.getMember(request);

        Member postMember = memberRepository.findMemberById(user_id);

        List<Post> posts = postRepository.findAllByMember(postMember);
        List<PostResponseDto> userPostResponseDtos = new ArrayList<>();

        for (Post post : posts) {
            Content content = contentRepository.findContentByPost(post);
            List<IngredientResponseDto> ingredientResponseDtos = postService.getIngredientByPost(post);
            userPostResponseDtos.add(postService.getResponsePostUserLike(loginMember, post, content, ingredientResponseDtos));
        }
        return ResponseDto.success(userPostResponseDtos);
    }

    public ResponseDto<?> follow(HttpServletRequest request, Long member_id) {

        //로그인 토큰 유효성 검증하기
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        Member followMember = memberRepository.findMemberById(member_id);
        if(member.getId() == followMember.getId()){
            return ResponseDto.fail(SAME_PERSON);              //예외처리
        }

        Follow follow = followRepository.findFollowByFollowerIdAndMember(member.getId(), followMember);

        //팔로우가 안되어있으면 팔로우
        if (follow == null) {
            Follow followUser = new Follow(member, followMember);
            followRepository.save(followUser);

            return ResponseDto.success("follow success");
            //팔로우가 되어있으면
        } else {
            followRepository.delete(follow);

            return ResponseDto.success("unfollow success");
        }
    }

    public ResponseDto<?> getFollower(HttpServletRequest request){

        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        List<Follow> followerlist = followRepository.findAllByMemberOrderByFollowerNicknameAsc(member);
        List<Member> members = new ArrayList<>();
        List<FollowResponseDto> followerResponseDtos = new ArrayList<>();

        for(Follow follower : followerlist){
            Member member2 = memberRepository.findMemberById(follower.getFollowerId());
            members.add(member2);
        }

        for(Member member2 : members){
            FollowResponseDto followerResponseDto = new FollowResponseDto(member2);
            followerResponseDtos.add(followerResponseDto);
        }

        return ResponseDto.success(followerResponseDtos);
    }

    public ResponseDto<?> getFollow(HttpServletRequest request) {
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        List<Follow> followList = followRepository.findAllByFollowerIdOrderByFollowNicknameAsc(member.getId());
        List<Member> members = new ArrayList<>();
        List<FollowResponseDto> followResponseDtos = new ArrayList<>();

        for(Follow follow : followList){
            Member member1 = memberRepository.findMemberById(follow.getMember().getId());
            members.add(member1);
        }

        for(Member member1 : members){
            FollowResponseDto followResponseDto = new FollowResponseDto(member1);
            followResponseDtos.add(followResponseDto);
        }

        return ResponseDto.success(followResponseDtos);

    }

    public ResponseDto<?> getOthersFollow(Long user_id) {

        Member member = memberRepository.findMemberById(user_id);

        List<Follow> followList = followRepository.findAllByFollowerIdOrderByFollowNicknameAsc(member.getId());
        List<Member> members = new ArrayList<>();
        List<FollowResponseDto> followResponseDtos = new ArrayList<>();

        for(Follow follow : followList){
            Member member1 = memberRepository.findMemberById(follow.getMember().getId());
            members.add(member1);
        }

        for(Member member1 : members){
            FollowResponseDto followResponseDto = new FollowResponseDto(member1);
            followResponseDtos.add(followResponseDto);
        }

        return ResponseDto.success(followResponseDtos);

        }

    public ResponseDto<?> getOthersFollower(Long user_id) {

        Member member = memberRepository.findMemberById(user_id);

        List<Follow> followerlist = followRepository.findAllByMemberOrderByFollowerNicknameAsc(member);
        List<Member> members = new ArrayList<>();
        List<FollowResponseDto> followerResponseDtos = new ArrayList<>();

        for(Follow follower : followerlist){
            Member member2 = memberRepository.findMemberById(follower.getFollowerId());
            members.add(member2);
        }

        for(Member member2 : members){
            FollowResponseDto followerResponseDto = new FollowResponseDto(member2);
            followerResponseDtos.add(followerResponseDto);
        }

        return ResponseDto.success(followerResponseDtos);
    }
}

