package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Request.PostRecipeRequestDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.PostDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class PostDetailController {
    private final PostDetailService postDetailService;

    @GetMapping("/api/post/{post_id}")
    public ResponseDto<?> getRecipe(@PathVariable Long post_id, HttpServletRequest request){
        return postDetailService.getRecipe(post_id, request);
    }

    //상세페이지 -> 해당 게시글 댓글 GET
    @GetMapping("/api/post/{post_id}/comment")
    public ResponseDto<?> getRecipeComment(@PathVariable Long post_id){
        return postDetailService.getRecipeComment(post_id);
    }

    //상세페이지 -> 페이지 별로 데이터 GET
    @GetMapping("/api/post/{post_id}/{page}")
    public ResponseDto<?> getRecipeByPage(@PathVariable Long post_id, @PathVariable int page){
        return postDetailService.getRecipeByPage(post_id, page);
    }

    //상세페이지 -> 레시피 등록 POST
    @PostMapping("/api/auth/post")
    public ResponseDto<?> postRecipe(@RequestBody PostRecipeRequestDto requestDto, HttpServletRequest request){
        return postDetailService.postRecipe(requestDto, request);
    }
    @PutMapping("/api/auth/post/{post_id}")
    public ResponseDto<?> putRecipe(@RequestBody PostRecipeRequestDto requestDto, HttpServletRequest request,
                                    @PathVariable Long post_id){
        return postDetailService.putRecipe(requestDto, request, post_id);
    }

    @PostMapping("/api/post/image")
    public ResponseDto<?> postImage(@RequestPart(value = "file") MultipartFile multipartFile){
        return postDetailService.postImage(multipartFile);
    }

    @DeleteMapping("/api/auth/post/{post_id}")
    public ResponseDto<?> deleteRecipe(HttpServletRequest request, @PathVariable Long post_id){
        return postDetailService.deleteRecipe(request, post_id);
    }
}
