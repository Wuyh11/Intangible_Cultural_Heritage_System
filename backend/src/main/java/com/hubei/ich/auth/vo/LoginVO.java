package com.hubei.ich.auth.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginVO {

    private String token;
    private String username;
    private String nickname;
    private List<String> roles;
}
