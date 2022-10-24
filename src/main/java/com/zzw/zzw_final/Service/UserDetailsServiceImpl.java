package com.zzw.zzw_final.Service;


import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String[] oauthList = email.split(",");
        Optional<Member> member = memberRepository.findMemberByOauthAndEmail(oauthList[1], oauthList[0]);
        return member
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

}