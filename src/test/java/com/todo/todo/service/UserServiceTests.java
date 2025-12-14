package com.todo.todo.service;

import com.todo.todo.entity.UserEntity;
import com.todo.todo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @Test
    void shouldCreateUserWhenUsernameIsUnique() {
        // Given
        String username = "newuser";
        String password = "password";
        String encodedPassword = "encodedPassword";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // When
        UserEntity result = userService.createUser(username, password);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(encodedPassword, result.getPassword());
        verify(userRepository).existsByUsername(username);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        // Given
        String username = "existinguser";
        String password = "password";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(username, password);
        });

        // Then
        assertEquals("Пользователь с таким логином уже существует", exception.getMessage());
        verify(userRepository).existsByUsername(username);
        verify(userRepository, never()).save(any());
    }

}
