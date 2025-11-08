package com.todo.todo.controller;

import com.todo.todo.entity.TaskEntity;
import com.todo.todo.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/tasks")
    public String getTasks(Model model) {
        List<TaskEntity> tasks = taskService.findAll();
        model.addAttribute("tasks", tasks);
        return "task-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        TaskEntity task = new TaskEntity();
        model.addAttribute("task", task);
        return "new-task";
    }

    @PostMapping("/save")
    public String saveTask(@Valid @ModelAttribute TaskEntity task, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new RuntimeException(getValidationErrorMessage(bindingResult));
        }
        taskService.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
        return "redirect:/tasks";
    }

    @GetMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id) {
        taskService.markAsCompleted(id);
        return "redirect:/tasks";
    }

    @GetMapping("/incomplete/{id}")
    public String incompleteTask(@PathVariable Long id) {
        taskService.markAsIncomplete(id);
        return "redirect:/tasks";
    }

    private String getValidationErrorMessage(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return bindingResult.getFieldError().getDefaultMessage();
        }
        return "Ошибка валидации";
    }
}