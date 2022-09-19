package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Request.PostRecipeRequestDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/api/auth/post")
    public ResponseDto<?> postRecipe(@RequestPart(value = "data") PostRecipeRequestDto requestDto, HttpServletRequest request,
                                     @RequestPart(value = "file") MultipartFile multipartFile){
        return postService.postRecipe(requestDto, request, multipartFile);
    }
}
