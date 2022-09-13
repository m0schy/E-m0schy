package com.m0schy.service;

import java.util.List;

import javax.mail.MessagingException;

import com.m0schy.domain.User;
import com.m0schy.exception.domain.EmailExistException;
import com.m0schy.exception.domain.UserNotFoundException;
import com.m0schy.exception.domain.UsernameExistException;

public interface UserService {

	User register(String firstname, String lastname, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;
	
	List<User> getUsers();
	
	User findByUsername(String username);
	
	User findUserByEmail(String email);
}
