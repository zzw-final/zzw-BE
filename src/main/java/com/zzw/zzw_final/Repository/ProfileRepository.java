package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.Profile;
import com.zzw.zzw_final.Dto.Entity.ProfileList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
