package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Request.*;
import com.zzw.zzw_final.Dto.Response.BestTagResponseDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.MemberService;
import com.zzw.zzw_final.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //메인페이지 -> 베스트 게시물 보여주기
    @GetMapping("/api/post")
    public ResponseDto<?> getBestRecipe(HttpServletRequest request){
        return postService.getBestRecipe(request);
    }


    //메인페이지 - 검색 (제목)
    @GetMapping("/api/post/filter/title")
    public ResponseDto<?> filterPostTitle(@RequestParam(name = "title") String title,
                                          HttpServletRequest request){
        return postService.filterPostTitle(title, request);
    }

    //메인페이지 - 검색 (닉네임)
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

    //상세페이지 -> GET
    @GetMapping("/api/post/{post_id}")
    public ResponseDto<?> getRecipe(@PathVariable Long post_id, HttpServletRequest request){
        return postService.getRecipe(post_id, request);
    }

    //상세페이지 -> 해당 게시글 댓글 GET
    @GetMapping("/api/post/{post_id}/comment")
    public ResponseDto<?> getRecipeComment(@PathVariable Long post_id){
        return postService.getRecipeComment(post_id);
    }

    //상세페이지 -> 페이지 별로 데이터 GET
    @GetMapping("/api/post/{post_id}/{page}")
    public ResponseDto<?> getRecipeByPage(@PathVariable Long post_id, @PathVariable int page){
        return postService.getRecipeByPage(post_id, page);
    }

    //상세페이지 -> 레시피 등록 POST
    @PostMapping("/api/auth/post")
    public ResponseDto<?> postRecipe(@RequestBody PostRecipeRequestDto requestDto, HttpServletRequest request){
        return postService.postRecipe(requestDto, request);
    }



    @PutMapping("/api/auth/post/{post_id}")
    public ResponseDto<?> putRecipe(@RequestBody PostRecipeRequestDto requestDto, HttpServletRequest request,
                                    @PathVariable Long post_id){
        return postService.putRecipe(requestDto, request, post_id);
    }



    @PostMapping("/api/post/image")
    public ResponseDto<?> postImage(@RequestPart(value = "file") MultipartFile multipartFile){
        return postService.postImage(multipartFile);
    }

    @DeleteMapping("/api/auth/post/{post_id}")
    public ResponseDto<?> deleteRecipe(HttpServletRequest request, @PathVariable Long post_id){
        return postService.deleteRecipe(request, post_id);
    }

    //메인페이지 - 하단바 (태그)
    @GetMapping("/api/post/filter")
    public ResponseDto<?> getAllTag(){
        return postService.getAllTag();
    }

    //모든 페이지 - 게시글 좋아요
    @PostMapping("/api/auth/post/{post_id}")
    public ResponseDto<?> postLike(@PathVariable Long post_id, HttpServletRequest request){
        return postService.postLike(post_id, request);
    }


}
