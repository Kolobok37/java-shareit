package ru.practicum.shareit.usersTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.DuplicateEmailDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.MapperUser;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserService mockUserService;

    @Test
    public void createUser_SuccessfulWhenUserCorrect() {
        User user = new User(null, "alex", "alex@gmail.com");
        Mockito.when(userStorage.createUser(user)).thenReturn(new User(Long.valueOf(1), "alex", "alex@gmail.com"));

        UserDto userTest = mockUserService.createUser(user);

        assertEquals(userTest, new UserDto(Long.valueOf(1), "alex", "alex@gmail.com"));
    }

    @Test
    public void createUser_ErrorWhenUsersEmailDuplicate() {
        User user = new User(null, "alex", "alex@gmail.com");
        Mockito.when(userStorage.createUser(user)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DuplicateEmailDataException.class, () -> mockUserService.createUser(user), "This email already exists");
    }

    @Test
    public void getUser_SuccessfulWhenUserGetting() {
        User user = new User((long) 1, "alex", "alex@gmail.com");
        Mockito.when(userStorage.getUser(Mockito.anyLong())).thenReturn(user);

        assertEquals(mockUserService.getUser((long) 1), new UserDto(Long.valueOf(1), "alex", "alex@gmail.com"));
    }

    @Test
    public void getUser_DontFind() {
        Mockito.when(userStorage.getUser(Mockito.anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> mockUserService.getUser((long) 1), "User not found.");
    }

    @Test
    public void updateUser_DontFind() {
        Mockito.when(userStorage.getUser(Mockito.anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> mockUserService.getUser((long) 1), "User not found.");
    }

    @Test
    public void updateUser_ErrorWhenUsersEmailDuplicate() {
        User user = new User((long) 1, "alex", "alex@gmail.com");
        Mockito.when(userStorage.createUser(user)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DuplicateEmailDataException.class, () -> mockUserService.createUser(user), "This email already exists");
    }

    @Test
    public void updateUser_SuccessfulWhenNameAndEmailIsPresent() {
        User oldUser = new User((long) 1, "alex", "alex@gmail.com");
        User newUser = new User((long) 1, "alex1", "alex1@gmail.com");
        Mockito.when(userStorage.createUser(newUser)).thenReturn(newUser);

        UserDto actualUser = mockUserService.updateUser(newUser);
        assertEquals(actualUser,MapperUser.mapToUserDto(newUser));
    }

    @Test
    public void updateUser_SuccessfulWhenNameOrEmailIsNullOrBlank() {
        User oldUser = new User((long) 1, "alex", "alex@gmail.com");
        User newUser = new User((long) 1, null, "alex1@gmail.com");
        Mockito.when(userStorage.getUser(Mockito.anyLong())).thenReturn(oldUser);
        Mockito.when(userStorage.createUser(newUser)).thenReturn(newUser);


        UserDto actualUser = mockUserService.updateUser(newUser);
        assertEquals("alex",actualUser.getName());
        assertEquals("alex1@gmail.com",actualUser.getEmail());

        newUser.setName("   ");
        actualUser = mockUserService.updateUser(newUser);
        assertEquals("alex",actualUser.getName());
        assertEquals("alex1@gmail.com",actualUser.getEmail());

        newUser.setEmail(null);
        actualUser = mockUserService.updateUser(newUser);
        assertEquals("alex",actualUser.getName());
        assertEquals("alex@gmail.com",actualUser.getEmail());

        newUser.setName("alex1");
        actualUser = mockUserService.updateUser(newUser);
        assertEquals("alex1",actualUser.getName());
        assertEquals("alex@gmail.com",actualUser.getEmail());

        newUser.setEmail("   ");
        actualUser = mockUserService.updateUser(newUser);
        assertEquals("alex1",actualUser.getName());
        assertEquals("alex@gmail.com",actualUser.getEmail());
    }

    @Test
    public void updateUser_SuccessfulWhenUserNameIsEmptyOrBlankOrNull() {
        User user = new User((long) 1, "alex", "alex@gmail.com");
        User userEmpty = new User((long) 1, "", "alex@gmail.com");
        User userBlank = new User((long) 1, " ", "alex@gmail.com");
        User userNull = new User((long) 1, null, "alex@gmail.com");

        Mockito.when(userStorage.createUser(user)).thenReturn(new User(Long.valueOf(1), "alex", "alex@gmail.com"));
        Mockito.when(userStorage.getUser(Mockito.anyLong())).thenReturn(new User(Long.valueOf(1), "alex", "alex@gmail.com"));

        UserDto userDto = new UserDto((long) 1, "alex", "alex@gmail.com");
        UserDto userEmptyDto = mockUserService.updateUser(userEmpty);
        UserDto userBlankDto = mockUserService.updateUser(userBlank);
        UserDto userNullDto = mockUserService.updateUser(userNull);

        assertEquals(userEmptyDto, userDto);
        assertEquals(userBlankDto, userDto);
        assertEquals(userNullDto, userDto);
    }

    @Test
    public void getAllUser_StorageEntity() {
        Mockito.when(userStorage.getAllUser()).thenReturn(new ArrayList<>());

        List<UserDto> users = mockUserService.getAllUser();

        assertTrue(users.isEmpty());
    }

    @Test
    public void getAllUser_FindAllUsersInStorage() {
        User user1 = new User((long) 1, "alex", "alex@gmail.com");
        User user2 = new User((long) 2, "oleg", "oleg@gmail.com");
        Mockito.when(userStorage.getAllUser()).thenReturn(new ArrayList<>(List.of(user1, user2)));

        assertEquals(mockUserService.getAllUser(), new ArrayList<>(List.of(MapperUser.mapToUserDto(user1), MapperUser.mapToUserDto(user2))));
    }
}
