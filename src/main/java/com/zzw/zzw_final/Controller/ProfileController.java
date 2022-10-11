package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/api/member/profile")
    public ResponseDto<?> getMemberProfile(){
        return profileService.getMemberProfile();
    }

    @PutMapping("/api/member/profile/{profileId}")
    public ResponseDto<?> updateMemberProfile(HttpServletRequest request, @PathVariable Long profileId){
        return profileService.updateMemberProfile(request, profileId);
    }
}
