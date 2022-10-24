package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @PostMapping("/api/auth/mypage/follow/{member_id}")
    public ResponseDto<?> follow(HttpServletRequest request, @PathVariable Long member_id){
        return followService.follow(request, member_id);
    }

    @GetMapping("/api/auth/mypage/follower")
    public ResponseDto<?> getFollower(HttpServletRequest request){

        return followService.getFollower(request);
    }

    @GetMapping("/api/auth/mypage/follow")
    public ResponseDto<?> getFollow(HttpServletRequest request){

        return followService.getFollow(request);
    }

    @GetMapping("/api/mypage/{user_id}/follow")
    public ResponseDto<?> getOthersFollow(@PathVariable Long user_id,HttpServletRequest request){
        return followService.getOthersFollow(user_id, request);
    }

    @GetMapping("/api/mypage/{user_id}/follower")
    public ResponseDto<?> getOthersFollower(@PathVariable Long user_id, HttpServletRequest request){
        return followService.getOthersFollower(user_id, request);
    }
}
