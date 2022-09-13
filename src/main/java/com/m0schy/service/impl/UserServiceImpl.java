package com.m0schy.service.impl;

import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.m0schy.domain.User;
import com.m0schy.domain.UserPrincipal;
import com.m0schy.enumeration.Role;
import com.m0schy.exception.domain.EmailExistException;
import com.m0schy.exception.domain.UserNotFoundException;
import com.m0schy.exception.domain.UsernameExistException;
import com.m0schy.repository.UserRepository;
import com.m0schy.service.EmailService;
import com.m0schy.service.LoginAttemptService;
import com.m0schy.service.UserService;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private LoginAttemptService loginAttemptService;
	@Autowired
	private EmailService emailService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByUsername(username);
		if(user == null) {
			throw new UsernameNotFoundException("User not found by username: " + username);
		} else {
			validateLoginAttempt(user);
			user.setLastLoginDateDiplay(user.getLastLoginDate());
			user.setLastLoginDate(new Date());
			userRepository.save(user);
			UserPrincipal userPrincipal = new UserPrincipal(user);
			return userPrincipal;
		}
	}

	private void validateLoginAttempt(User user) {
		if(user.isNotLocked()) {
			if(loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
				user.setNotLocked(false);
			} else {
				user.setNotLocked(true);
			}
		} else {
			loginAttemptService.evictUserFromLogingAttemptCache(user.getUsername());
		}
		
	}

	@Override
	public User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
		validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
		User user = new User();
		user.setUserId(generateUserId());
		String password = generatePassword();
		String encodedPassword = encodePassword(password);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);
		user.setEmail(email);
		user.setJoinDate(new Date());
		user.setPassword(encodedPassword);
		user.setActive(true);
		user.setNotLocked(true);
		user.setRole(Role.ROLE_USER.name());
		user.setAuthorities(Role.ROLE_USER.getAuthorities());
		user.setProfileImgUrl(getTemporaryProfileImageUrl());
		userRepository.save(user);
		emailService.sendNewPasswordEmail(firstName, password, email);
		return user;
	}

	private String getTemporaryProfileImageUrl() {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/profile/temp").toUriString();
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	private String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(10);
	}

	private String generateUserId() {
		return RandomStringUtils.randomNumeric(10);
	}

	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findUserByUsername(username);
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}
	
	private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String email) throws UserNotFoundException, UsernameExistException, EmailExistException {
		if(StringUtils.isNotBlank(currentUsername)) {
			User currentUser = findByUsername(currentUsername);
			if(currentUser == null) {
				throw new UserNotFoundException("No user found by username : " + currentUsername);
			}
			User userByUsername = findByUsername(newUsername);
			if(userByUsername != null && !currentUser.getId().equals(userByUsername.getId())) {
				throw new UsernameExistException("Username already exists");
			}
			User userByEmail = findUserByEmail(email);
			if(userByEmail != null && !currentUser.getId().equals(userByEmail.getId())) {
				throw new EmailExistException("Email already exists");
			}
			return currentUser;
		} else {
			User userByUsername = findByUsername(newUsername);
			if(userByUsername != null) {
				throw new UsernameExistException("Username already exists");
			}
			User userByEmail = findUserByEmail(email);
			if(userByEmail != null) {
				throw new EmailExistException("Email already exists");
			}
			return userByUsername;
		}
		
	}

}
