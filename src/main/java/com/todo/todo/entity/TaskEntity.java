package com.todo.todo.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;

import lombok.Data;
import java.time.LocalDate;

@Data
@Table(name = "tasks")
public class TaskEntity {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@NotBlank(message = "Название обязательно")
private String title;

private String description;

@NotBlank(message = "Категория обязательна")
private String category;

private Priority priority;

@FutureOrPresent(message = "Дата не может быть истекшей")
private LocalDate deadline;

private boolean completed;

}
