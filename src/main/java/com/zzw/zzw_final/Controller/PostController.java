package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.MemberService;
import com.zzw.zzw_final.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final MemberService memberService;

    @GetMapping("/api/post/tag")
    public ResponseDto<?> getBestTag(){
        return postService.getBestTagList();
    }
    @GetMapping("/api/post/recent")
    public ResponseDto<?> getRecentRecipe(@RequestParam(value = "lastPostId", required = false) Long lastPostId,
                                          @RequestParam(value = "isLast", required = false) Boolean isLast){
        if(isLast != null&& isLast == true)
            return ResponseDto.success("");
        return postService.getRecentRecipeInfinite(lastPostId);
    }

    @GetMapping("/api/auth/post/recent")
    public ResponseDto<?> getAuthRecentRecipe(HttpServletRequest request){
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();
        return postService.getRecentRecipeTop10(member);
    }
    @GetMapping("/api/auth/post/follow")
    public ResponseDto<?> getFollowRecipe(HttpServletRequest request,
                                          @RequestParam(value = "lastPostId", required = false) Long lastPostId,
                                          @RequestParam(value = "isLast", required = false) Boolean isLast){
        if(isLast!=null && isLast==true)
            return ResponseDto.success("");
        return postService.getFollowRecipe(request, lastPostId);
    }
    @GetMapping("/api/post/best")
    public ResponseDto<?> getBestRecipe(HttpServletRequest request){
        return postService.getBestRecipe(request);
    }

    @GetMapping("/api/post/filter/title")
    public ResponseDto<?> filterPostTitle(@RequestParam(name = "title") String title,
                                          HttpServletRequest request){
        return postService.filterPostTitle(title, request);
    }

    @GetMapping("/api/post/filter/nickname")
    public ResponseDto<?> filterPostNickname(@RequestParam(name = "nickname") String nickname,
                                             HttpServletRequest request){
        return postService.filterPostNickname(nickname, request);
    }

    @GetMapping("/api/post/filter/tag")
    public ResponseDto<?> filterPostTag(@RequestParam(name = "tag")String tag,
                                        HttpServletRequest request){
        return postService.filterPostTag(tag, request);
    }

    @GetMapping("/api/post/filter")
    public ResponseDto<?> getAllTag(){
        return postService.getAllTag();
    }

    @PostMapping("/api/auth/post/{post_id}")
    public ResponseDto<?> postLike(@PathVariable Long post_id, HttpServletRequest request){
        return postService.postLike(post_id, request);
    }

    @GetMapping("/api/auth/mypage/myposts")
    public ResponseDto<?> getUserPosts(HttpServletRequest request){
        return postService.getUserPost(request);
    }


    @GetMapping("/api/auth/mypage/likeposts")
    public ResponseDto<?> getUserLikePosts(HttpServletRequest request){
        return postService.getUserLikePosts(request);
    }

    @GetMapping("/api/mypage/{user_id}/myposts")
    public ResponseDto<?> getOtherUserPosts(@PathVariable Long user_id, HttpServletRequest request){
        return postService.getOtherUserPosts(user_id, request);
    }


}
