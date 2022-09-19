package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Request.FilterPostByTitleRequestDto;
import com.zzw.zzw_final.Dto.Request.PostRecipeRequestDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //메인페이지 -> 베스트 게시물 보여주기
    @GetMapping("/api/post")
    public ResponseDto<?> getBestRecipe(){
        return postService.getBestRecipe();
    }


    //메인페이지 - 검색 (제목)
    @GetMapping("/api/post/filter/title")
    public ResponseDto<?> filterPostTitle(@RequestBody FilterPostByTitleRequestDto requestDto){
        return postService.filterPostTitle(requestDto);
    }


    //상세페이지 -> 레시피 등록 POST
    @PostMapping("/api/auth/post")
    public ResponseDto<?> postRecipe(@RequestPart(value = "data") PostRecipeRequestDto requestDto, HttpServletRequest request,
                                     @RequestPart(value = "file") MultipartFile multipartFile){
        return postService.postRecipe(requestDto, request, multipartFile);
    }

    @PutMapping("/api/auth/post/{post_id}")
    public ResponseDto<?> putRecipe(@RequestPart(value = "data") PostRecipeRequestDto requestDto, HttpServletRequest request,
                                     @RequestPart(value = "file", required = false) MultipartFile multipartFile, @PathVariable Long post_id){
        return postService.putRecipe(requestDto, request, multipartFile, post_id);
    }

    @DeleteMapping("/api/auth/post/{post_id}")
    public ResponseDto<?> delteRecipe(HttpServletRequest request, @PathVariable Long post_id){
        return postService.deleteRecipe(request, post_id);
    }
}
