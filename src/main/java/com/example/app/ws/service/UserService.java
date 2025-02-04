package com.example.app.ws.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.app.ws.shared.dto.UserDTO;

public interface UserService extends UserDetailsService{
	UserDTO createUser(UserDTO user);
	UserDTO getUser(String email);
	UserDTO getUserByUserId(String userId);
	UserDTO updateUser(String userId, UserDTO userDto);
	void deleteUser(String userId);
	List<UserDTO> getUsers(int page, int limit);
}
