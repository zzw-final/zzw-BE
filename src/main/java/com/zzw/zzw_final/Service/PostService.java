package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Dto.Entity.*;
import com.zzw.zzw_final.Dto.Request.*;

import com.zzw.zzw_final.Dto.ErrorCode;
import com.zzw.zzw_final.Dto.Response.*;
import com.zzw.zzw_final.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final MemberService memberService;
    private final FileUploaderService fileUploaderService;
    private final TagListRepository tagListRepository;
    private final TagRepository tagRepository;
    private final ContentRepository contentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final PostLikeRepository postLikeRepository;

    private final CommentRepository commentRepository;

    public ResponseDto<?> getBestRecipe(HttpServletRequest request){

        Member member = memberService.getMember(request);

        //베스트만 보여주기 (팔로우, 팔로워 X)
        if (member == null) {
            //제일 등록된 태그 상위 5개 리스트에 담기
            List<Tag> tags = tagRepository.findAllByOrderByCountDesc();
            List<BestTagResponseDto> tagResponseDtos = new ArrayList<>();

            for (int i = 0; i < 5; i++)
                tagResponseDtos.add(new BestTagResponseDto(tags.get(i)));

            //제일 좋아요 많은 순으로 레시피 리스트에 담기
            List<Post> best_posts = postRepository.findAllByOrderByLikeNumDesc();
            List<PostResponseDto> best_postResponseDtos = new ArrayList<>();
            if (best_posts.size() < 10) {
                for (int i = 0; i < best_posts.size(); i++) {
                    List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(best_posts.get(i));
                    best_postResponseDtos.add(new PostResponseDto(best_posts.get(i), ingredientResponseDtos));
                }
            } else {
                for (int i = 0; i < 10; i++) {
                    List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(best_posts.get(i));
                    best_postResponseDtos.add(new PostResponseDto(best_posts.get(i), ingredientResponseDtos));
                }
            }

            //제일 최신 순으로 레시피 리스트에 담기
            List<Post> recent_posts = postRepository.findAllByOrderByCreatedAtDesc();
            List<PostResponseDto> recent_postResponseDtos = new ArrayList<>();

            if (recent_posts.size() < 10) {
                for (int i = 0; i < recent_posts.size(); i++) {
                    List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(recent_posts.get(i));
                    recent_postResponseDtos.add(new PostResponseDto(recent_posts.get(i), ingredientResponseDtos));
                }
            } else {
                for (int i = 0; i < 10; i++) {
                    List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(recent_posts.get(i));
                    recent_postResponseDtos.add(new PostResponseDto(recent_posts.get(i), ingredientResponseDtos));
                }
            }

            MainPostResponseDto mainPostResponseDto = new MainPostResponseDto(tagResponseDtos, best_postResponseDtos, recent_postResponseDtos);
            return ResponseDto.success(mainPostResponseDto);
        } else {
            //제일 등록된 태그 상위 5개 리스트에 담기
            List<Tag> tags = tagRepository.findAllByOrderByCountDesc();
            List<BestTagResponseDto> tagResponseDtos = new ArrayList<>();

            for (int i = 0; i < 5; i++)
                tagResponseDtos.add(new BestTagResponseDto(tags.get(i)));

            //제일 좋아요 많은 순으로 레시피 리스트에 담기
            List<Post> best_posts = postRepository.findAllByOrderByLikeNumDesc();
            List<PostResponseDto> best_postResponseDtos = new ArrayList<>();

            if (best_posts.size() < 10) {
                for (int i = 0; i < best_posts.size(); i++) {
                    List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(best_posts.get(i));
                    best_postResponseDtos.add(getResponsePostUserLike(member, best_posts.get(i), ingredientResponseDtos));
                }
            } else {
                for (int i = 0; i < 10; i++) {
                    List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(best_posts.get(i));
                    best_postResponseDtos.add(getResponsePostUserLike(member, best_posts.get(i), ingredientResponseDtos));
                }
            }

            //제일 최신 순으로 레시피 리스트에 담기
            List<Post> recent_posts = postRepository.findAllByOrderByCreatedAtDesc();
            List<PostResponseDto> recent_postResponseDtos = new ArrayList<>();

            if (recent_posts.size() < 10) {
                for (int i = 0; i < recent_posts.size(); i++) {
                    List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(recent_posts.get(i));
                    recent_postResponseDtos.add(getResponsePostUserLike(member, recent_posts.get(i), ingredientResponseDtos));
                }
            } else {
                for (int i = 0; i < 10; i++) {
                    List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(recent_posts.get(i));
                    recent_postResponseDtos.add(getResponsePostUserLike(member, recent_posts.get(i), ingredientResponseDtos));
                }
            }

            //팔로우 레시피 리스트 팔로우 당 1개씩 보내기
            List<Follow> follows = followRepository.findAllByFollowerId(member.getId());
            List<Post> followPost = new ArrayList<>();

            for (Follow follow : follows) {
                Long followId = follow.getMember().getId();
                Member followmember = memberRepository.findMemberById(follow.getMember().getId());
                List<Post> userPost = postRepository.findAllByMemberOrderByCreatedAtDesc(followmember);
                if (userPost.size() != 0) {
                    followPost.add(userPost.get(0));
                }
            }

            List<PostResponseDto> follow_postResponseDtos = new ArrayList<>();

            for (Post post : followPost) {
                List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(post);
                follow_postResponseDtos.add(getResponsePostUserLike(member, post, ingredientResponseDtos));
            }

            MainPostResponseDto mainPostResponseDto = new MainPostResponseDto(tagResponseDtos, best_postResponseDtos,
                    recent_postResponseDtos, follow_postResponseDtos);
            return ResponseDto.success(mainPostResponseDto);
        }
    }

    // 유저가 해당 게시물에 좋아요 했는지 여부를 판단해서 Dto로 반환하는 함수
    public PostResponseDto getResponsePostUserLike (Member member, Post
            post, List < IngredientResponseDto > ingredientResponseDtos){
        PostLike postLike = postLikeRepository.findPostLikesByPostAndMember(post, member);
        if (postLike == null) {
            return new PostResponseDto(post, ingredientResponseDtos);
        } else {
            return new PostResponseDto(post, ingredientResponseDtos, true);
        }
    }

    public List<IngredientResponseDto> getIngredientByPost (Post post){
        List<TagList> tagLists = tagListRepository.findAllByPost(post);
        List<IngredientResponseDto> ingredientResponseDtos = new ArrayList<>();
        for (TagList tagList : tagLists)
            ingredientResponseDtos.add(new IngredientResponseDto(tagList));

        return ingredientResponseDtos;
    }

    public ResponseDto<?> filterPostTitle (String title, HttpServletRequest request){

        Member member = memberService.getMember(request);

        List<Post> posts = postRepository.findAllByTitleContaining(title);
        List<PostResponseDto> Posts = new ArrayList<>();

        for (Post post : posts) {
            List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(post);
            if (member != null)
                Posts.add(getResponsePostUserLike(member, post, ingredientResponseDtos));
            else
                Posts.add(new PostResponseDto(post, ingredientResponseDtos, false));
        }

        return ResponseDto.success(Posts);
    }

    public ResponseDto<?> filterPostNickname (String nickname, HttpServletRequest request){
        Member loginMember = memberService.getMember(request);

        List<Member> members = memberRepository.findAllByNicknameContaining(nickname);
        List<PostResponseDto> responseDtos = new ArrayList<>();

        for (Member member : members) {
            List<Post> posts = postRepository.findAllByMember(member);
            for (Post post : posts) {
                List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(post);
                if (loginMember != null)
                    responseDtos.add(getResponsePostUserLike(loginMember, post, ingredientResponseDtos));
                else
                    responseDtos.add(new PostResponseDto(post, ingredientResponseDtos));
            }
        }
        return ResponseDto.success(responseDtos);
    }


    public ResponseDto<?> getAllTag () {

        List<Tag> tags = tagRepository.findAllByOrderByCountDesc();
        List<BestTagResponseDto> tagResponseDtos = new ArrayList<>();
        if (tags.size() < 100) {
            for (Tag tag : tags) {
                tagResponseDtos.add(new BestTagResponseDto(tag));
            }
        } else {
            for (int i = 0; i < 100; i++) {
                tagResponseDtos.add(new BestTagResponseDto(tags.get(i)));
            }
        }
        return ResponseDto.success(tagResponseDtos);
    }

    public ResponseDto<?> filterPostTag (String tag, HttpServletRequest request){
        Member loginMember = memberService.getMember(request);

        List<Post> response_posts = new ArrayList<>();
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        String[] tag_list = tag.split(",");

        for (Post post : posts) {
            if (isPostinTag(post, tag_list)) {
                response_posts.add(post);
            }
        }

        List<PostResponseDto> responseDtos = new ArrayList<>();
        for (Post post : response_posts) {
            List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(post);
            if (loginMember != null)
                responseDtos.add(getResponsePostUserLike(loginMember, post, ingredientResponseDtos));
            else
                responseDtos.add(new PostResponseDto(post, ingredientResponseDtos));
        }

        return ResponseDto.success(responseDtos);
    }
    public Boolean isPostinTag (Post post, String[]tagList){
        int count = 0;

        for (int i = 0; i < tagList.length; i++) {
            String tag = tagList[i];

            for (TagList postTag : post.getTagLists()) {
                if (postTag.getName().equals(tag)) {
                    count++;
                }
            }
        }
        if (count >= tagList.length)
            return true;
        else
            return false;
    }

    public ResponseDto<?> postLike (Long post_id, HttpServletRequest request){
        //로그인 토큰 유효성 검증하기
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        Post post = postRepository.findPostById(post_id);

        if (post == null) {
            return ResponseDto.fail(ErrorCode.NOTFOUND_POST_ID);
        }

        //이전에 해당 게시글에 좋아요를 한 적이 있는지 판단
        PostLike postLike = postLikeRepository.findPostLikesByPostAndMember(post, member);

        // 이전에 이 게시물에 좋아요를 한 적이 없음 -> 좋아요 수락
        if (postLike == null) {
            PostLike userLike = new PostLike(member, post);
            postLikeRepository.save(userLike);

            post.setLikeNum(postLikeRepository.countAllByPost(post).intValue());  // -> likeNum 업데이트
            postRepository.save(post);

            return ResponseDto.success("post like success");
        }
        // 이전에 이 게시물에 좋아요를 한 적이 있음 -> 좋아요 취소
        else {
            postLikeRepository.delete(postLike);

            post.setLikeNum(postLikeRepository.countAllByPost(post).intValue());  // -> likeNum 업데이트
            postRepository.save(post);

            return ResponseDto.success("post like delete success");
        }
    }

}


