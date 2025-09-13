package com.todo.todo.repository;

import com.todo.todo.entity.Priority;
import com.todo.todo.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository <TaskEntity, Long>{
    List<TaskEntity> findByCategory(String category);
    List<TaskEntity> findByPriotity(Priority priority);
    List<TaskEntity> findbyCompleted(boolean completed);
}
