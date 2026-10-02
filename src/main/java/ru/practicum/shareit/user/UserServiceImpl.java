package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() != null) {
            for (User existing : userStorage.getAllUsers()) {
                if (existing.getEmail().equalsIgnoreCase(userDto.getEmail())) {
                    throw new EmailAlreadyExistsException("Email должен быть уникальным");
                }
            }
        }

        User user = UserMapper.toUser(userDto);

        User saved = userStorage.addUser(user);

        return UserMapper.toUserDto(saved);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existing = userStorage.getUserById(userId);
        if (existing == null) {
            throw new NoSuchElementException("Пользователь не найден");
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equals(existing.getEmail())) {
            for (User other : userStorage.getAllUsers()) {
                if (!other.getId().equals(userId) && other.getEmail().equalsIgnoreCase(userDto.getEmail())) {
                    throw new EmailAlreadyExistsException("Email должен быть уникальным");
                }
            }
        }

        if (userDto.getName() != null) {
            existing.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            existing.setEmail(userDto.getEmail());
        }

        User updated = userStorage.patchUser(existing);

        return UserMapper.toUserDto(updated);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NoSuchElementException("Пользователь не найден");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}
