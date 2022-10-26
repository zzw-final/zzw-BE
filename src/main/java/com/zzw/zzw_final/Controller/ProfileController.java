package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Request.UpdateMemberRequestDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/api/member/profile")
    public ResponseDto<?> getMemberProfile(){
        return profileService.getMemberProfile();
    }

    @PutMapping("/api/member/update")
    public ResponseDto<?> updateMemberProfile(HttpServletRequest request, @RequestBody UpdateMemberRequestDto requestDto){
        return profileService.updateMemberProfile(request, requestDto);
    }
}
