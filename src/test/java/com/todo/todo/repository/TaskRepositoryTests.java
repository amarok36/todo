package com.todo.todo.repository;

import com.todo.todo.entity.Priority;
import com.todo.todo.entity.TaskEntity;
import com.todo.todo.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class TaskRepositoryTests {
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;
    private UserEntity anotherUser;
    private TaskEntity testTask;

    @BeforeEach
    void setUp() {
        // первый тестовый пользователь
        testUser = new UserEntity();
        testUser.setUsername("testuser1");
        testUser.setPassword("password1");
        entityManager.persistAndFlush(testUser);

        // второй тестовый пользователь
        anotherUser = new UserEntity();
        anotherUser.setUsername("testuser2");
        anotherUser.setPassword("password2");
        entityManager.persistAndFlush(anotherUser);

        // задача для первого тестового пользователя
        testTask = new TaskEntity();
        testTask.setTitle("Задача 1 для первого пользователя");
        testTask.setDescription("Тестовое описание");
        testTask.setCategory("Работа");
        testTask.setPriority(Priority.MEDIUM);
        testTask.setDeadline(LocalDate.now().plusDays(7));
        testTask.setCompleted(false);
        testTask.setUser(testUser);
        entityManager.persistAndFlush(testTask);

        // ещё одна задача для первого тестового пользователя
        TaskEntity anotherTask = new TaskEntity();
        anotherTask.setTitle("Задача 2 для первого пользователя");
        anotherTask.setDescription("Другое тестовое описание");
        anotherTask.setCategory("Личное");
        anotherTask.setPriority(Priority.HIGH);
        anotherTask.setDeadline(LocalDate.now().plusDays(3));
        anotherTask.setCompleted(true);
        anotherTask.setUser(testUser);
        entityManager.persistAndFlush(anotherTask);

        // задача для второго тестового пользователя
        TaskEntity otherUserTask = new TaskEntity();
        otherUserTask.setTitle("Задача 1 для второго пользователя");
        otherUserTask.setDescription("Ещё одно тестовое описание");
        otherUserTask.setCategory("Работа");
        otherUserTask.setPriority(Priority.LOW);
        otherUserTask.setDeadline(LocalDate.now().plusDays(5));
        otherUserTask.setCompleted(false);
        otherUserTask.setUser(anotherUser);
        entityManager.persistAndFlush(otherUserTask);
    }

    @Test
    void findByUser_WhenUserHasTasks_ShouldReturnUserTasks() {
        // When
        List<TaskEntity> userTasks = taskRepository.findByUser(testUser);

        // Then
        assertThat(userTasks).hasSize(2);
        assertThat(userTasks)
                .extracting(TaskEntity::getTitle)
                .containsExactlyInAnyOrder("Задача 1 для первого пользователя", "Задача 2 для первого пользователя");
        assertThat(userTasks)
                .allMatch(task -> task.getUser().getId().equals(testUser.getId()));
    }

    @Test
    void findByUser_WhenUserHasNoTasks_ShouldReturnEmptyList() {
        // Given
        UserEntity newUser = new UserEntity();
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        entityManager.persistAndFlush(newUser);

        // When
        List<TaskEntity> userTasks = taskRepository.findByUser(newUser);

        // Then
        assertThat(userTasks).isEmpty();
    }

    @Test
    void findByUser_ShouldReturnOnlySpecifiedUserTasks() {
        // When
        List<TaskEntity> anotherUserTasks = taskRepository.findByUser(anotherUser);

        // Then
        assertThat(anotherUserTasks).hasSize(1);
        assertThat(anotherUserTasks.get(0).getTitle()).isEqualTo("Задача 1 для второго пользователя");
        assertThat(anotherUserTasks.get(0).getUser().getId()).isEqualTo(anotherUser.getId());
    }

    @Test
    void saveTask_ShouldPersistTask() {
        // Given
        TaskEntity newTask = new TaskEntity();
        newTask.setTitle("Новая задача");
        newTask.setDescription("Новое описание");
        newTask.setCategory("Учеба");
        newTask.setPriority(Priority.HIGH);
        newTask.setDeadline(LocalDate.now().plusDays(10));
        newTask.setCompleted(false);
        newTask.setUser(testUser);

        // When
        TaskEntity savedTask = taskRepository.save(newTask);
        entityManager.flush();
        entityManager.clear();

        // Then
        TaskEntity foundTask = entityManager.find(TaskEntity.class, savedTask.getId());
        assertThat(foundTask).isNotNull();
        assertThat(foundTask.getTitle()).isEqualTo("Новая задача");
        assertThat(foundTask.getDescription()).isEqualTo("Новое описание");
        assertThat(foundTask.getCategory()).isEqualTo("Учеба");
        assertThat(foundTask.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(foundTask.getDeadline()).isEqualTo(LocalDate.now().plusDays(10));
        assertThat(foundTask.isCompleted()).isFalse();
        assertThat(foundTask.getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void findById_WhenTaskExists_ShouldReturnTask() {
        // When
        Optional<TaskEntity> foundTask = taskRepository.findById(testTask.getId());

        // Then
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getTitle()).isEqualTo("Задача 1 для первого пользователя");
        assertThat(foundTask.get().getUser().getUsername()).isEqualTo("testuser1");
    }

    @Test
    void findById_WhenTaskNotExists_ShouldReturnEmpty() {
        // When
        Optional<TaskEntity> foundTask = taskRepository.findById(999L);

        // Then
        assertThat(foundTask).isEmpty();
    }

    @Test
    void deleteById_ShouldRemoveTask() {
        // Given
        Long taskId = testTask.getId();

        // When
        taskRepository.deleteById(taskId);
        entityManager.flush();
        entityManager.clear();

        // Then
        TaskEntity foundTask = entityManager.find(TaskEntity.class, taskId);
        assertThat(foundTask).isNull();
    }

    @Test
    void findAll_ShouldReturnAllTasks() {
        // When
        List<TaskEntity> allTasks = taskRepository.findAll();

        // Then
        assertThat(allTasks).hasSize(3);
    }

    @Test
    void updateTask_ShouldUpdateExistingTask() {
        // Given
        testTask.setTitle("Обновленный заголовок для задачи 1 для первого пользователя");
        testTask.setCompleted(true);

        // When
        TaskEntity updatedTask = taskRepository.save(testTask);
        entityManager.flush();
        entityManager.clear();

        // Then
        TaskEntity foundTask = entityManager.find(TaskEntity.class, testTask.getId());
        assertThat(foundTask.getTitle()).isEqualTo("Обновленный заголовок для задачи 1 для первого пользователя");
        assertThat(foundTask.isCompleted()).isTrue();
    }
}
