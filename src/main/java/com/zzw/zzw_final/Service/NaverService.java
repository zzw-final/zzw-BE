package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Dto.Response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class NaverService {
    public ResponseDto<?> naverLogin(String authCode, HttpServletResponse response) {
        return ResponseDto.success("login");
    }
}
