package com.todo.todo.repository;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");


    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setUsername("testuser");
        testUser.setPassword("testpassword");
        entityManager.persistAndFlush(testUser);
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // When
        Optional<UserEntity> foundUser = userRepository.findByUsername("testuser");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getPassword()).isEqualTo("testpassword");
    }

    @Test
    void findByUsername_WhenUserNotExists_ShouldReturnEmpty() {
        // When
        Optional<UserEntity> foundUser = userRepository.findByUsername("nonexistentuser");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void saveUser_ShouldPersistUser() {
        // Given
        UserEntity newUser = new UserEntity();
        newUser.setUsername("newuser");
        newUser.setPassword("newpassword");

        // When
        UserEntity savedUser = userRepository.save(newUser);
        entityManager.flush();
        entityManager.clear();

        // Then
        UserEntity foundUser = entityManager.find(UserEntity.class, savedUser.getId());
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("newuser");
        assertThat(foundUser.getPassword()).isEqualTo("newpassword");
    }
}
