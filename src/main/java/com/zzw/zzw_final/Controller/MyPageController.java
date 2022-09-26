package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Entity.Follow;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.MypageService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final MypageService mypageService;

    //유저에게 칭호 POST
    @PostMapping("/api/grade/{grade_id}/{user_id}")
    public ResponseDto<?> postGrade(HttpServletRequest request, @PathVariable Long grade_id,
                                    @PathVariable Long user_id){
        return mypageService.postGrade(request, grade_id, user_id);
    }

    //마이페이지 유저정보 GET
    @GetMapping("/api/auth/mypage")
    public ResponseDto<?> getUserInfo(HttpServletRequest request){
        return mypageService.getUserInfo(request);
    }

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


    //팔로우 관련
    @PostMapping("/api/auth/mypage/follow/{member_id}")
    public ResponseDto<?> follow(HttpServletRequest request, @PathVariable Long member_id){
        return mypageService.follow(request, member_id);
    }


    @GetMapping("/api/auth/mypage/follower")
    public ResponseDto<?> getFollower(HttpServletRequest request){

        return mypageService.getFollower(request);
    }

    @GetMapping("/api/auth/mypage/follow")
    public ResponseDto<?> getFollow(HttpServletRequest request){
        return mypageService.getFollow(request);
    }


}
