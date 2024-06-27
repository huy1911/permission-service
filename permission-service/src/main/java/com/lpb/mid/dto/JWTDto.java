package com.lpb.mid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JWTDto {
    private String jti;
    private List<String> roles;
    private String userName;
    private String appId;
    private String customerNo;
    private String whiteListIp;
    private String branchCode;
    private String secretKey;
}
