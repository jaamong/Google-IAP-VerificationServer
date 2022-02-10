package com.umc.btos.src;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.model.ProductPurchase;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class Payment {

    public Payment() throws IOException, GeneralSecurityException, GoogleJsonResponseException {
        //TODO 1. GoogleCredential 생성
        //     2. API 호출


        // ==================== GoogleCredential 생성 ====================
        //credential 생성 전 필요한 변수 선언
        String emailAddress = "themusic025@gmail.com"; // 서비스 계정을 생성하면서 발급받은 email address //임시로 에러 안나게하려고 채워넣음, emailAddress를 yml에 넣어둬야할까..?
        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        HttpTransport httpTransport = new NetHttpTransport(); //GoogleNetHttpTransport.newTrustedTransport();

        //본격적인 GoogleCredential 생성, (json이든 p12이든 application.yml에 저장해서 @Value로 받아오는게 좋을듯)
        // P12 비밀키 파일 방식
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(emailAddress)
                .setServiceAccountPrivateKeyFromP12File(new File("P12File이위치한경로"))
                .setServiceAccountScopes(Collections.singleton("https://www.googleapis.com/auth/androidpublisher"))
                .build();

        // JSON 비밀키 파일 방식
        InputStream jsonInputStream = ResourceUtils.getURL();//InputStream 타입으로 json 파일 저장, ()안에 json 파일이 위치한 URL 입력

        GoogleCredential googleCredential = GoogleCredential.fromStream(jsonInputStream, httpTransport, JSON_FACTORY)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/androidpublisher"))
                .createDelegated(emailAddress);


        // ======================== API 호출 ========================
        String packageName = ""; //인앱 상품이 판매된 애플리케이션의 패키지 이름
        String productId = ""; //인앱 상품 SKㅋU
        String purchaseToken = ""; //안드로이드에서 받아올 구매 토큰

        AndroidPublisher publisher = new AndroidPublisher.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(packageName)
                .build();

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
    }
}
