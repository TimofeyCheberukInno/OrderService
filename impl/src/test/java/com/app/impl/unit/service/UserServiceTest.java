package com.app.impl.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.impl.Client.UserClient;
import com.app.impl.dto.user.UserResponseDto;
import com.app.impl.exception.NoSuchUserException;
import com.app.impl.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("Tests for getUserByEmail(String email)")
    class GetUserByEmail {
        private UserResponseDto userResponseDto =
                new UserResponseDto(
                        1L,
                        "test_name",
                        "test_surname",
                        LocalDate.of(2000, 12, 12),
                        "test@gmail.com"
                );

        @Test
        @DisplayName("return user by email")
        public void shouldReturnUserByEmail() {
            Mockito.when(userClient.getUserByEmail("test@gmail.com"))
                    .thenReturn(userResponseDto);

            UserResponseDto actualValue = userService.getUserByEmail("test@gmail.com");

            assertThat(actualValue).isEqualTo(userResponseDto);

            Mockito.verify(userClient, Mockito.times(1))
                    .getUserByEmail("test@gmail.com");
        }

        @Test
        @DisplayName("return NoSuchUserException")
        public void shouldReturnNoSuchUserException() {
            Mockito.when(userClient.getUserByEmail("test@gmail.com"))
                    .thenReturn(null);

            assertThatExceptionOfType(NoSuchUserException.class)
                    .isThrownBy(() -> userService.getUserByEmail("test@gmail.com"));

            Mockito.verify(userClient, Mockito.times(1))
                    .getUserByEmail("test@gmail.com");
        }
    }
}
