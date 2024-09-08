package ru.practicum.ewm.users.service;

import ru.practicum.ewm.users.entity.User;

import java.util.List;

public interface UserService {

    User addUser(User user);

    List<User> getUsers(List<Long> userIds, Integer from, Integer size);

    void deleteUser(Long userId);
}
