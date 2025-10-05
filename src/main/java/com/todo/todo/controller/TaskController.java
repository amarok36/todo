package com.todo.todo.controller;

import com.todo.todo.entity.Priority;
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
    public String getTasks(@RequestParam(required = false) String category,
                           @RequestParam(required = false) String priority,
                           @RequestParam(required = false) Boolean completed,
                           Model model) {
        List<TaskEntity> tasks;
        if(category != null && !category.isEmpty()) {
            tasks = taskService.findByCategory(category);
        } else if (priority != null && !priority.isEmpty()) {
            tasks = taskService.findByPriority(Priority.valueOf(priority));
        } else if(completed != null) {
            tasks = taskService.findByCompleted(completed);
        } else {
            tasks = taskService.findAll();
        }

        model.addAttribute("tasks", tasks);
        return "task-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("task", new TaskEntity());
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

    private String getValidationErrorMessage(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return bindingResult.getFieldError().getDefaultMessage();
        }
        return "Неизвестная ошибка валидации";
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
}