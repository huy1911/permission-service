package com.lpb.mid.service.impl;


import com.lpb.mid.Utils.Constants;
import com.lpb.mid.config.cache.RedisStorage;
import com.lpb.mid.dto.JWTDto;
import com.lpb.mid.dto.SendKafkaDto;
import com.lpb.mid.entity.UsersEntity;
import com.lpb.mid.repo.UserRepositorys;
import com.lpb.mid.service.PermissionService;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
public class PermissionServiceImpl implements PermissionService {
    private static final Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);

    @Autowired
    private UserRepositorys userRepositorys;
    private final RedisStorage<JWTDto> accStorage;

    public PermissionServiceImpl(RedissonClient client) {
        this.accStorage = new RedisStorage<>(client);
    }

    @Override
    public JWTDto getDtoLogin(SendKafkaDto accRequestDTO) {
        JWTDto jwtDto ;
        jwtDto = accStorage.getValue(Constants.Authorization_customer, accRequestDTO.getUsername() + accRequestDTO.getCustomerNo());
        if(jwtDto == null) {
            String secretKey;
            try {
                UsersEntity usersEntity = userRepositorys.findByUserNameAndStatus(accRequestDTO.getUsername(), Constants.Open);
                if (ObjectUtils.isEmpty(usersEntity)) {
                    logger.error("getSecretKey : users not found --->{} ", accRequestDTO.getUsername());
                    return jwtDto;
                }
                secretKey = usersEntity.getSecretKey();
                jwtDto = com.lpb.mid.dto.JWTDto.builder()
                        .whiteListIp(usersEntity.getWhiteListIp())
                        .appId(usersEntity.getAppId())
                        .customerNo(accRequestDTO.getCustomerNo())
                        .userName(accRequestDTO.getUsername())
                        .secretKey(secretKey)
                        .build();
                accStorage.put(Constants.Authorization_customer, accRequestDTO.getUsername() + accRequestDTO.getCustomerNo(), jwtDto);
                logger.info("getDtoLogin :get tokenDTO ---->{}", jwtDto);
            } catch (Exception e) {
                logger.error("getDtoLogin : get getDtoLogin fail --->{}", e.getMessage());
                return jwtDto;
            }
        }
        return jwtDto;
    }

    @Override
    public String getUsername(HttpServletRequest request) {
        String userName = null;
        try {
            String authorization = request.getParameter(Constants.Authorization) != null ? request.getParameter(Constants.Authorization) : request.getHeader(Constants.Authorization);
            if (authorization != null) {
                try {
                    logger.info("getUsername :get token by user");
                    authorization = URLDecoder.decode(authorization, StandardCharsets.UTF_8.toString());
                } catch (UnsupportedEncodingException e) {
                    logger.error("getUsername :get token by user error");
                    throw new RuntimeException(e);
                }
            }
            if (StringUtils.hasText(authorization) && authorization.startsWith(Constants.Bearer)) {
                authorization = authorization.substring(7);
            }
            if (authorization != null && validateJwtToken(authorization)) {
                userName = Jwts.parser().setSigningKey("JwtSecretKey").parseClaimsJws(authorization).getBody().getSubject();
            }
            return userName;
        } catch (Exception e) {
            logger.info("getUserName :get userName error ");
            return userName;
        }
    }

    @Override
    public String getSecretKey(String username) {
        try {
            UsersEntity usersEntity = userRepositorys.findByUserNameAndStatus(username, Constants.Open);
            if (ObjectUtils.isEmpty(usersEntity)) {
                logger.error("getSecretKey : users not found --->{} ", username);
                return null;
            }
            return usersEntity.getSecretKey();
        } catch (Exception exception) {
            logger.error("getSecretKey : get secretKey fail by user --->{} ", username);
            return null;
        }

    }

    public static boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey("JwtSecretKey").parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
