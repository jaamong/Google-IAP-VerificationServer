package com.umc.btos.src.googleOauth;

import com.umc.btos.src.googleOauth.model.GoogleOauth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final GoogleOauth googleOauth;
    private final HttpServletResponse response;

    /**
     * 구글 로그인 처리
     */
    public void request() {
        String redirectURL = googleOauth.getOauthRedirectURL();

        try {
            response.sendRedirect(redirectURL); //토큰 요청을 하도록 리다이렉트
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 로그인 처리 후 토큰 요청
     * @param code
     * @return
     */
    public String requestAccessToken(String code) {
        return googleOauth.requestAccessToken(code);
    }

}
