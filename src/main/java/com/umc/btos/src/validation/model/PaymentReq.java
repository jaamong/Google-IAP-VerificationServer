package com.umc.btos.src.validation.model;

import lombok.Getter;

@Getter

public class PaymentReq {
    private String packageName; //인앱 상품이 판매된 애플리케이션의 패키지 이름
    private String productId; //인앱 상품 SKU
    private String purchaseToken; //안드로이드에서 받아올 구매 토큰
}
