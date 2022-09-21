package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.MypageService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final MypageService mypageService;

    @GetMapping("/api/auth/mypage")
    public ResponseDto<?> getUserInfo(HttpServletRequest request){
        return mypageService.getUserInfo(request);
    }

    @PostMapping("/api/auth/grade/{grade_id}")
    public ResponseDto<?> postGrade(HttpServletRequest request, @PathVariable Long grade_id){
        return mypageService.postGrade(request, grade_id);
    }
}
