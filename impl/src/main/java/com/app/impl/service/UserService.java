package com.app.impl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.impl.exception.NoSuchUserException;
import com.app.impl.dto.user.UserResponseDto;
import com.app.impl.Client.UserClient;

@Service
public class UserService {
    private final UserClient userClient;

    @Autowired
    public UserService(UserClient userClient) {
        this.userClient = userClient;
    }

    public UserResponseDto getUserByEmail(String email) {
        UserResponseDto user = userClient.getUserByEmail(email);
        if (user == null) {
            throw new NoSuchUserException(email);
        }
        return user;
    }
}
