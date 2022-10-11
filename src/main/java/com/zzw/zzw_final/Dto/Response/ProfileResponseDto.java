package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.ProfileList;
import lombok.Getter;

@Getter
public class ProfileResponseDto {
    private Long profileId;
    private String imageUrl;

    public ProfileResponseDto(ProfileList profileList) {
        this.profileId = profileList.getId();
        this.imageUrl = profileList.getProfile();
    }
}
