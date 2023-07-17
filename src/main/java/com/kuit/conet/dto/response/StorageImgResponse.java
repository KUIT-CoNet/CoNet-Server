package com.kuit.conet.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StorageImgResponse {
    private String imgFileName;
    private String imgUrl;
}