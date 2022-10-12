package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Dto.Entity.*;
import com.zzw.zzw_final.Dto.ErrorCode;
import com.zzw.zzw_final.Dto.Response.*;
import com.zzw.zzw_final.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final MemberService memberService;
    private final TagListRepository tagListRepository;
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final PostLikeRepository postLikeRepository;

    public ResponseDto<?> getBestRecipe(HttpServletRequest request){

        Member member = memberService.getMember(request);

        if (member == null) {

            MainPostResponseDto mainPostResponseDto = new MainPostResponseDto(getBestTagList(),
                    getBestRecipeTop10(null), getRecentRecipeTop10(null));

            return ResponseDto.success(mainPostResponseDto);

        } else {

            List<Follow> follows = followRepository.findAllByFollowerId(member.getId());
            List<Post> followPost = new ArrayList<>();

            for (Follow follow : follows) {
                Member followMember = memberRepository.findMemberById(follow.getMember().getId());
                List<Post> userPost = postRepository.findAllByMemberOrderByCreatedAtDesc(followMember);
                if (userPost.size() != 0) {
                    followPost.add(userPost.get(0));
                }
            }

            List<PostResponseDto> follow_postResponseDtos = new ArrayList<>();

            for (Post post : followPost) {
                List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(post);
                follow_postResponseDtos.add(getResponsePostUserLike(member, post, ingredientResponseDtos));
            }

            MainPostResponseDto mainPostResponseDto = new MainPostResponseDto(getBestTagList(), getBestRecipeTop10(member),
                    getRecentRecipeTop10(member), follow_postResponseDtos);
            return ResponseDto.success(mainPostResponseDto);
        }
    }

    private List<PostResponseDto> getRecentRecipeTop10(Member member) {
        List<Post> recent_posts = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostResponseDto> recent_postResponseDtos = new ArrayList<>();

        int postSize = (recent_posts.size() < 10) ? recent_posts.size() : 10;

        if (member == null){
            for (int i = 0; i < recent_posts.size(); i++) {
                List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(recent_posts.get(i));
                recent_postResponseDtos.add(new PostResponseDto(recent_posts.get(i), ingredientResponseDtos));
            }
        }
        else{
            for (int i = 0; i < postSize; i++) {
                List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(recent_posts.get(i));
                recent_postResponseDtos.add(getResponsePostUserLike(member, recent_posts.get(i), ingredientResponseDtos));
            }
        }

        return recent_postResponseDtos;
    }

    private List<PostResponseDto> getBestRecipeTop10(Member member) {
        List<Post> best_posts = postRepository.findAllByOrderByLikeNumDesc();
        List<PostResponseDto> best_postResponseDtos = new ArrayList<>();

        int postSize = (best_posts.size() < 10) ? best_posts.size() : 10;

        if (member == null){
            for (int i = 0; i < postSize; i++) {
                List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(best_posts.get(i));
                best_postResponseDtos.add(new PostResponseDto(best_posts.get(i), ingredientResponseDtos));
            }
        }
        else{
            for (int i = 0; i < postSize; i++) {
                List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(best_posts.get(i));
                best_postResponseDtos.add(getResponsePostUserLike(member, best_posts.get(i), ingredientResponseDtos));
            }
        }

        return best_postResponseDtos;
    }

    private List<BestTagResponseDto> getBestTagList() {
        List<Tag> tags = tagRepository.findAllByOrderByCountDesc();
        List<BestTagResponseDto> tagResponseDtos = new ArrayList<>();

        for (int i = 0; i < 5; i++)
            tagResponseDtos.add(new BestTagResponseDto(tags.get(i)));

        return tagResponseDtos;
    }

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

        int tagSize = (tags.size() < 100) ? tags.size() : 100;

        for (int i = 0; i < tagSize; i++) {
            tagResponseDtos.add(new BestTagResponseDto(tags.get(i)));
        }
        return ResponseDto.success(tagResponseDtos);
    }

    public ResponseDto<?> filterPostTag (String tag, HttpServletRequest request){
        Member loginMember = memberService.getMember(request);

        List<Post> response_posts = new ArrayList<>();
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        String[] tag_list = tag.split(",");

        for (Post post : posts)
            if (isPostInTag(post, tag_list))
                response_posts.add(post);

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
    public Boolean isPostInTag(Post post, String[]tagList){
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

        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        Post post = postRepository.findPostById(post_id);

        if (post == null) {
            return ResponseDto.fail(ErrorCode.NOTFOUND_POST_ID);
        }

        PostLike postLike = postLikeRepository.findPostLikesByPostAndMember(post, member);

        if (postLike == null) {
            PostLike userLike = new PostLike(member, post);
            postLikeRepository.save(userLike);
        }
        else
            postLikeRepository.delete(postLike);

        post.setLikeNum(postLikeRepository.countAllByPost(post).intValue());
        postRepository.save(post);

        return ResponseDto.success("post like success");
    }

    public ResponseDto<?> getUserPost(HttpServletRequest request) {
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        List<Post> posts = postRepository.findAllByMember(member);
        List<PostResponseDto> userPostResponseDtos = new ArrayList<>();

        for (Post post : posts) {
            List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(post);
            userPostResponseDtos.add(getResponsePostUserLike(member, post, ingredientResponseDtos));
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
            List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(post);
            userPostResponseDtos.add(getResponsePostUserLike(member, post, ingredientResponseDtos));
        }

        return ResponseDto.success(userPostResponseDtos);
    }

    public ResponseDto<?> getOtherUserPosts(Long user_id, HttpServletRequest request) {
        Member loginMember = memberService.getMember(request);

        Member postMember = memberRepository.findMemberById(user_id);

        List<Post> posts = postRepository.findAllByMember(postMember);
        List<PostResponseDto> userPostResponseDtos = new ArrayList<>();

        for (Post post : posts) {
            List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(post);
            userPostResponseDtos.add(getResponsePostUserLike(loginMember, post, ingredientResponseDtos));
        }
        return ResponseDto.success(userPostResponseDtos);
    }
}