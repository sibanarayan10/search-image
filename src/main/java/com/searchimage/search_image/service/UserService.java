package com.searchimage.search_image.service;

import com.searchimage.search_image.dto.UserDto;
import com.searchimage.search_image.entity.User;

import java.util.List;

public interface UserService {
//    List<User> seedUsersAndUserRoles();

    User findUserById(Long userId);

    User findUserByEmail(String email);

    boolean deleteUser(Long id);

    User findUserByName(String username);

    boolean userExists(String username, String email);

    void saveUserWithUpdatedPassword(User userEntity);

    void registerUser(UserDto userDto);


}



