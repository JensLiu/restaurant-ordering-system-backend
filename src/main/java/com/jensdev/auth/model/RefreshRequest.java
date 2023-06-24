package com.jensdev.auth.model;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}
