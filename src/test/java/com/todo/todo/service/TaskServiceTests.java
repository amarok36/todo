package com.todo.todo.service;

import com.todo.todo.entity.TaskEntity;
import com.todo.todo.entity.UserEntity;
import com.todo.todo.repository.TaskRepository;
import com.todo.todo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTests {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private TaskService taskService;

    private UserEntity testUser;
    private TaskEntity testTask;


    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        testTask = new TaskEntity();
        testTask.setId(1L);
        testTask.setTitle("Тестовая задача");
        testTask.setDescription("Тестовое описание");
        testTask.setCategory("Покупки");
        testTask.setCompleted(false);
        testTask.setUser(testUser);
    }

    @Test
    void shouldReturnUserTasksWhenUserExists() {
        // Given
        String username = "testuser";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(taskRepository.findByUser(testUser)).thenReturn(Arrays.asList(testTask));

        // When
        List<TaskEntity> result = taskService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask, result.get(0));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        String username = "nonexistent";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.findAll();
        });

        // Then
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository).findByUsername(username);
        verify(taskRepository, never()).findByUser(any());
    }

    @Test
    void shouldSaveTaskWithCurrentUserWhenTaskIsValid() {
        // Given
        TaskEntity newTask = new TaskEntity();
        newTask.setTitle("Тестовая задача");
        newTask.setDescription("Тестовое описание");
        newTask.setCategory("Работа");

        String username = "testuser";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> {
            TaskEntity savedTask = invocation.getArgument(0);
            savedTask.setId(2L);
            return savedTask;
        });

        // When
        TaskEntity result = taskService.save(newTask);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals("Тестовая задача", result.getTitle());
        verify(userRepository).findByUsername(username);
        verify(taskRepository).save(newTask);
    }

    @Test
    void shouldDeleteTaskWhenTaskExists() {
        // Given
        Long taskId = 1L;

        // When
        taskService.deleteById(taskId);

        // Then
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void shouldMarkTaskAsCompletedWhenTaskExists() {
        // Given
        Long taskId = 1L;
        testTask.setCompleted(false);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(testTask);

        // When
        taskService.markAsCompleted(taskId);

        // Then
        assertTrue(testTask.isCompleted());
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(testTask);
    }


}
