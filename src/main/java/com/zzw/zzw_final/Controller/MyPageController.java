package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final MypageService mypageService;

    //마이페이지 유저가 쓴 글 GET
    @GetMapping("/api/auth/mypage/myposts")
    public ResponseDto<?> getUserPosts(HttpServletRequest request){
        return mypageService.getUserPost(request);
    }

    //마이페이지 유저가 좋아요 한 글 GET
    @GetMapping("/api/auth/mypage/likeposts")
    public ResponseDto<?> getUserLikePosts(HttpServletRequest request){
        return mypageService.getUserLikePosts(request);
    }

    //마이페이지 해당 유저의 쓴 글 GET
    @GetMapping("/api/mypage/{user_id}/myposts")
    public ResponseDto<?> getOtherUserPosts(@PathVariable Long user_id, HttpServletRequest request){
        return mypageService.getOtherUserPosts(user_id, request);
    }

}
