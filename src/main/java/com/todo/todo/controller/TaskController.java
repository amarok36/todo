package com.todo.todo.controller;

import com.todo.todo.entity.Priority;
import com.todo.todo.entity.TaskEntity;
import com.todo.todo.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    // просмотр задач с фильтрацией
    @GetMapping("/")
    public String tasksList(@RequestParam(required = false) String category,
                            @RequestParam(required = false) String priority,
                            @RequestParam(required = false) Boolean completed,
                            Model model) {
        List<TaskEntity> tasks;
        if(category != null && !category.isEmpty()) {
            tasks = taskRepository.findByCategory(category);
        } else if (priority != null && !priority.isEmpty()) {
            tasks = taskRepository.findByPriority(Priority.valueOf(priority));
        } else if(completed != null) {
            tasks = taskRepository.findByCompleted(completed);
        } else {
            tasks = taskRepository.findAll();
        }

        model.addAttribute("tasks", tasks);
        return "task-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("task", new TaskEntity());
        return "task-form";
    }

    @PostMapping("/save")
    public String saveTask(@ModelAttribute TaskEntity task) {
        taskRepository.save(task);
        return "redirect:/";
    }

   @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Неверный id задачи: " + id));
        taskRepository.delete(task);
        return "redirect:/";
    }

    @GetMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Неверный id задачи: " + id));
        task.setCompleted(true);
        taskRepository.save(task);
        return "redirect:/";
    }

    @GetMapping("/incomplete/{id}")
    public String incompleteTask(@PathVariable Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Неверный id задачи: " + id));
        task.setCompleted(false);
        taskRepository.save(task);
        return "redirect:/";
    }
}
