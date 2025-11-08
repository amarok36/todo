package com.todo.todo.repository;

import com.todo.todo.entity.TaskEntity;
import com.todo.todo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    List<TaskEntity> findByUser(UserEntity user);
}
