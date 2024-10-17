package ru.practicum.ewm.users.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.users.dto.CreateUserDto;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.dto.UserShortDto;
import ru.practicum.ewm.users.entity.User;

import java.util.List;

@Component
public class UserMapper {

    public List<UserDto> toUserDto(List<User> users) {
        return users.stream().map(this::toUserDto).toList();
    }

    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }

    public User toUser(CreateUserDto createUserDto) {
        return new User(
                createUserDto.getName(),
                createUserDto.getEmail()
        );
    }
}
