package com.umc.btos.src.validation;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.model.ProductPurchase;
import com.google.api.services.plus.Plus;
import com.umc.btos.src.googleOauth.OauthController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shops")
public class controller {

    private final OauthController oauthController;

    @ResponseBody
    @GetMapping("/receipt-validation")
    public String validationReceipt(String token) {

        // ======================= 구글 로그인 =======================
        oauthController.googleLogin();
            //로그인하면서 받은 accessToken을 저기 아래 넣어주자
            //아니면 안드에서 login API를 먼저 호출하고 accessToken을 받게한 후 이 API를 호출할 때 토큰을 넘겨주도록 할까?

        // ================= Google Credential 생성 =================
        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        HttpTransport httpTransport = new NetHttpTransport(); //GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
        /*
        Plus plus = new Plus.Builder(httpTransport,
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Google-PlusSample/1.0")
                .build();
        */

        // ======================== API 호출 ========================
        String packageName = ""; //인앱 상품이 판매된 애플리케이션의 패키지 이름
        String productId = ""; //인앱 상품 SKU
        String purchaseToken = token; //안드로이드에서 받아올 구매 토큰

        AndroidPublisher publisher = new AndroidPublisher.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(packageName)
                .build();

        try {
            AndroidPublisher.Purchases.Products.Get get = publisher.purchases().products().get(packageName, productId, purchaseToken); //inapp 아이템의 구매 및 소모 상태 확인
            ProductPurchase productPurchase = get.execute(); //검증 결과
            System.out.println(productPurchase.toPrettyString());

            // 인앱 상품의 소비 상태. 0 아직 소비 안됨(Yet to be consumed) / 1 소비됨(Consumed)
            Integer consumptionState = productPurchase.getConsumptionState();

            // 개발자가 지정한 임의 문자열 정보
            String developerPayload = productPurchase.getDeveloperPayload();

            // 구매 상태. 0 구매완료 / 1 취소됨
            Integer purchaseState = productPurchase.getPurchaseState();

            // 상품이 구매된 시각. 타임스탬프 형태
            Long purchaseTimeMillis = productPurchase.getPurchaseTimeMillis();

            return productPurchase.toPrettyString();
        } catch (IOException e) {
            return e.toString();
        }
    }
}
