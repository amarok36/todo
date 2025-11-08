package com.todo.todo.service;

import com.todo.todo.entity.TaskEntity;
import com.todo.todo.entity.UserEntity;
import com.todo.todo.repository.TaskRepository;
import com.todo.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        return userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public List<TaskEntity> findAll() {
        UserEntity currentUser = getCurrentUser();

        return taskRepository.findByUser(currentUser);
    }

    @Transactional
    public TaskEntity save(TaskEntity task) {
        UserEntity currentUser = getCurrentUser();
        task.setUser(currentUser);

        return taskRepository.save(task);
    }

    @Transactional
    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    @Transactional
    public void markAsCompleted(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Неверный id " + id));
        task.setCompleted(true);
        taskRepository.save(task);
    }

    @Transactional
    public void markAsIncomplete(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Неверный id " + id));
        task.setCompleted(false);
        taskRepository.save(task);
    }
}