package com.app.impl.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.impl.dto.user.UserResponseDto;

@FeignClient(name = "userService", url = "${user.service.url}")
public interface UserClient {
    @GetMapping("/api/users/by-email")
    UserResponseDto getUserByEmail(@RequestParam String email);
}
