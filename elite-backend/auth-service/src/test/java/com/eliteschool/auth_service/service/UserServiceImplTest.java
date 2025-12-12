package com.eliteschool.auth_service.service;

import com.eliteschool.auth_service.dto.UserDTO;
import com.eliteschool.auth_service.dto.request.UpdateUserRequestDTO;
import com.eliteschool.auth_service.model.User;
import com.eliteschool.auth_service.model.enums.RoleType;
import com.eliteschool.auth_service.repository.UserRepository;
import com.eliteschool.auth_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User();
        testUser.setEliteId(testUserId);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encoded_password");
        testUser.setRole(RoleType.STUDENT);
        testUser.setActive(true);
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User createdUser = userService.createUser(testUser);

        // Assert
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo("testuser");
        assertThat(createdUser.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void shouldGetUserByIdSuccessfully() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // Act
        User foundUser = userService.getUserById(testUserId);

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEliteId()).isEqualTo(testUserId);
        verify(userRepository, times(1)).findById(testUserId);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void shouldThrowExceptionWhenUserNotFoundById() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(nonExistentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        
        verify(userRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> foundUser = userService.findByUsername("testuser");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> foundUser = userService.findByEmail("test@example.com");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should check if username exists")
    void shouldCheckIfUsernameExists() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        boolean exists = userService.existsByUsername("testuser");

        // Assert
        assertThat(exists).isTrue();
        verify(userRepository, times(1)).existsByUsername("testuser");
    }

    @Test
    @DisplayName("Should check if email exists")
    void shouldCheckIfEmailExists() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        boolean exists = userService.existsByEmail("test@example.com");

        // Assert
        assertThat(exists).isTrue();
        verify(userRepository, times(1)).existsByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should get all users")
    void shouldGetAllUsers() {
        // Arrange
        User user2 = new User();
        user2.setEliteId(UUID.randomUUID());
        user2.setUsername("testuser2");
        user2.setEmail("test2@example.com");
        
        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> allUsers = userService.getAllUsers();

        // Assert
        assertThat(allUsers).hasSize(2);
        assertThat(allUsers).containsExactlyInAnyOrder(testUser, user2);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get all students")
    void shouldGetAllStudents() {
        // Arrange
        when(userRepository.findByRole(RoleType.STUDENT)).thenReturn(Arrays.asList(testUser));

        // Act
        List<UserDTO> students = userService.getAllStudents();

        // Assert
        assertThat(students).hasSize(1);
        assertThat(students.get(0).getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findByRole(RoleType.STUDENT);
    }

    @Test
    @DisplayName("Should get all faculty")
    void shouldGetAllFaculty() {
        // Arrange
        User faculty = new User();
        faculty.setEliteId(UUID.randomUUID());
        faculty.setUsername("faculty1");
        faculty.setEmail("faculty@example.com");
        faculty.setRole(RoleType.FACULTY);
        
        when(userRepository.findByRole(RoleType.FACULTY)).thenReturn(Arrays.asList(faculty));

        // Act
        List<UserDTO> facultyList = userService.getAllFaculty();

        // Assert
        assertThat(facultyList).hasSize(1);
        assertThat(facultyList.get(0).getUsername()).isEqualTo("faculty1");
        verify(userRepository, times(1)).findByRole(RoleType.FACULTY);
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Arrange
        UpdateUserRequestDTO updateDTO = new UpdateUserRequestDTO();
        updateDTO.setEmail("newemail@example.com");
        
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User updatedUser = userService.updateUser(testUserId, updateDTO);

        // Assert
        assertThat(updatedUser).isNotNull();
        verify(userRepository, times(1)).findById(testUserId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Arrange
        doNothing().when(userRepository).deleteById(testUserId);

        // Act
        userService.deleteUser(testUserId);

        // Assert
        verify(userRepository, times(1)).deleteById(testUserId);
    }
}

