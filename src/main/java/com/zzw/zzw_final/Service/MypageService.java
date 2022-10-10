package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Dto.Entity.*;
import com.zzw.zzw_final.Dto.Response.*;
import com.zzw.zzw_final.Repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.http.protocol.HTTP;
import org.springframework.stereotype.Service;

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
                followerlist.size(), followList.size(), responseDtos, true);

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

        List<GradeListResponseDto> responseDtos = new ArrayList<>();
        List<Grade> grades = gradeRepository.findAllByMember(member);
        for(Grade grade : grades){
            responseDtos.add(new GradeListResponseDto(grade.getGradeList()));
        }

        if(loginMember ==null){
            MypageUserInfoResponseDto responseDto = new MypageUserInfoResponseDto(member,
                    followerlist.size(), followList.size(), responseDtos, false);

            return ResponseDto.success(responseDto);
        }else{
            Follow follow = followRepository.findFollowByFollowerIdAndMember(loginMember.getId(), member);

            if (follow == null) {
                MypageUserInfoResponseDto responseDto = new MypageUserInfoResponseDto(member,
                        followerlist.size(), followList.size(), responseDtos,false);

                return ResponseDto.success(responseDto);

            } else {
                MypageUserInfoResponseDto responseDto = new MypageUserInfoResponseDto(member,
                        followerlist.size(), followList.size(), responseDtos,true);

                return ResponseDto.success(responseDto);
            }
        }

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
            FollowResponseDto followResponseDto = new FollowResponseDto(member1, true);
            followResponseDtos.add(followResponseDto);
        }

        return ResponseDto.success(followResponseDtos);
    }

    public ResponseDto<?> getFollower(HttpServletRequest request) {

        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        List<Follow> followerlist = followRepository.findAllByMemberOrderByFollowerNicknameAsc(member);
        List<Member> members = new ArrayList<>();
        List<FollowResponseDto> followerResponseDtos = new ArrayList<>();

        for (Follow follower : followerlist) {
            Member member2 = memberRepository.findMemberById(follower.getFollowerId());
            members.add(member2);
        }

        //나의 팔로워 중에 내가 팔로우한 사람이 있는지 판별
        for (Member member2 : members) {
            Follow follow = followRepository.findFollowByFollowerIdAndMember(member.getId(), member2);

            if (follow == null) {
                FollowResponseDto followerResponseDto = new FollowResponseDto(member2);
                followerResponseDtos.add(followerResponseDto);
            } else {
                FollowResponseDto followerResponseDto = new FollowResponseDto(member2, true);
                followerResponseDtos.add(followerResponseDto);
            }
        }
        return ResponseDto.success(followerResponseDtos);
    }

        //다른 유저 마이페이지에서 팔로우, 팔로워 목록보기
        public ResponseDto<?> getOthersFollow (Long user_id, HttpServletRequest request){

            Member loginMember1 = memberService.getMember(request);

            //로그인 안했을 때는 팔로우한 사람이 있는지 확인할 필요x
            if (loginMember1 == null) {
                Member member = memberRepository.findMemberById(user_id);

                List<Follow> followList = followRepository.findAllByFollowerIdOrderByFollowNicknameAsc(member.getId());
                List<Member> members = new ArrayList<>();
                List<FollowResponseDto> followResponseDtos = new ArrayList<>();

                for (Follow follow : followList) {
                    Member member1 = memberRepository.findMemberById(follow.getMember().getId());
                    members.add(member1);
                }

                for (Member member1 : members) {
                    FollowResponseDto followResponseDto = new FollowResponseDto(member1);
                    followResponseDtos.add(followResponseDto);
                }
                return ResponseDto.success(followResponseDtos);

            } else {

                Member member = memberRepository.findMemberById(user_id);

                List<Follow> followList = followRepository.findAllByFollowerIdOrderByFollowNicknameAsc(member.getId());
                List<Member> members = new ArrayList<>();
                List<FollowResponseDto> followResponseDtos = new ArrayList<>();

                for (Follow follow : followList) {
                    Member member1 = memberRepository.findMemberById(follow.getMember().getId());
                    members.add(member1);
                }

                for (Member member1 : members) {
                    Follow follow = followRepository.findFollowByFollowerIdAndMember(loginMember1.getId(), member1);

                    if (follow == null) {
                        FollowResponseDto followerResponseDto = new FollowResponseDto(member1);
                        followResponseDtos.add(followerResponseDto);
                    } else {
                        FollowResponseDto followerResponseDto = new FollowResponseDto(member1, true);
                        followResponseDtos.add(followerResponseDto);
                    }
                }
                return ResponseDto.success(followResponseDtos);
            }
        }

    public ResponseDto<?> getOthersFollower(Long user_id, HttpServletRequest request) {

        Member loginMember = memberService.getMember(request);

        //로그인 안했을 때는 null값
        if (loginMember == null) {
            Member member = memberRepository.findMemberById(user_id);

            List<Follow> followerlist = followRepository.findAllByMemberOrderByFollowerNicknameAsc(member);
            List<Member> members = new ArrayList<>();
            List<FollowResponseDto> followerResponseDtos = new ArrayList<>();

            for (Follow follower : followerlist) {
                Member member2 = memberRepository.findMemberById(follower.getFollowerId());
                members.add(member2);
            }

            for (Member member2 : members) {
                FollowResponseDto followerResponseDto = new FollowResponseDto(member2);
                followerResponseDtos.add(followerResponseDto);
            }

            return ResponseDto.success(followerResponseDtos);

        } else {

            //로그인했을 때
            Member member = memberRepository.findMemberById(user_id);

            List<Follow> followerlist = followRepository.findAllByMemberOrderByFollowerNicknameAsc(member);
            List<Member> members = new ArrayList<>();
            List<FollowResponseDto> followerResponseDtos = new ArrayList<>();

            for (Follow follower : followerlist) {
                Member member2 = memberRepository.findMemberById(follower.getFollowerId());
                members.add(member2);
            }

            for (Member member2 : members) {
                Follow follow = followRepository.findFollowByFollowerIdAndMember(loginMember.getId(), member2);

                //loginMember가 member2를 팔로우했는지 안했는지 판별
                if(follow == null) {
                    FollowResponseDto followerResponseDto = new FollowResponseDto(member2);
                    followerResponseDtos.add(followerResponseDto);
                }else{
                    FollowResponseDto followerResponseDto = new FollowResponseDto(member2,true);
                    followerResponseDtos.add(followerResponseDto);
                }
            }

            return ResponseDto.success(followerResponseDtos);
        }
    }
}

