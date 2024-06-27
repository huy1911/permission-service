package com.lpb.mid.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lpb.mid.dto.JWTDto;
import com.lpb.mid.dto.SendKafkaDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PermissionService {
    JWTDto getDtoLogin(SendKafkaDto accRequestDTO) throws JsonProcessingException;
    String getUsername(HttpServletRequest request);
    String getSecretKey(String username);
}
