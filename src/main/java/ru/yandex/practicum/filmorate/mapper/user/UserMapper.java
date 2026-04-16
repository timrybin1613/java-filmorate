package ru.yandex.practicum.filmorate.mapper.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.dto.user.UserResponseDto;
import ru.yandex.practicum.filmorate.dto.user.UserUpdateDto;
import ru.yandex.practicum.filmorate.model.User;

@Component
public class UserMapper {

    public User toUser(UserCreateDto dto) {
        User user = new User();

        user.setLogin(dto.getLogin());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setBirthday(dto.getBirthday());

        return user;
    }

    public User toUser(UserUpdateDto dto) {
        User user = new User();

        user.setId(dto.getId());
        user.setLogin(dto.getLogin());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setBirthday(dto.getBirthday());

        return user;
    }

    public UserResponseDto toUserResponseDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setId(user.getId());
        userResponseDto.setLogin(user.getLogin());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setName(user.getName());
        userResponseDto.setBirthday(user.getBirthday());

        return userResponseDto;
    }
}
