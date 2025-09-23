package com.todo.todo.controller;

import com.todo.todo.entity.Priority;
import com.todo.todo.entity.TaskEntity;
import com.todo.todo.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/")
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
        return "task-form";
    }

    @PostMapping("/save")
    public String saveTask(@ModelAttribute TaskEntity task) {
        taskService.save(task);
        return "redirect:/";
    }

   @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id) {
        taskService.markAsCompleted(id);
        return "redirect:/";
    }

    @GetMapping("/incomplete/{id}")
    public String incompleteTask(@PathVariable Long id) {
       taskService.markAsIncomplete(id);
        return "redirect:/";
    }
}
