package com.todo.todo.service;

import com.todo.todo.entity.Priority;
import com.todo.todo.entity.TaskEntity;
import com.todo.todo.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<TaskEntity> findAll() {
        return taskRepository.findAll();
    }

    public List<TaskEntity> findByCategory(String category) {
        return taskRepository.findByCategory(category);
    }

    public List<TaskEntity> findByPriority(Priority priority) {
        return taskRepository.findByPriority(priority);
    }

    public List<TaskEntity> findByCompleted(boolean completed) {
        return taskRepository.findByCompleted(completed);
    }

    public Optional<TaskEntity> findById(Long id) {
        return taskRepository.findById(id);
    }

    @Transactional
    public TaskEntity save (TaskEntity task) {
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteById(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Неверный id " + id));
        taskRepository.deleteById(id);
    }

    @Transactional
    public void markAsCompleted(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Неверный id " + id));
        task.setCompleted(true);
        taskRepository.save(task);
    }

    @Transactional
    public void markAsIncomplete(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Неверный id " + id));
        task.setCompleted(false);
        taskRepository.save(task);
    }
}
