package com.m0schy.resource;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m0schy.constants.SecurityConstants;
import com.m0schy.domain.User;
import com.m0schy.domain.UserPrincipal;
import com.m0schy.exception.domain.EmailExistException;
import com.m0schy.exception.domain.ExceptionHandling;
import com.m0schy.exception.domain.UserNotFoundException;
import com.m0schy.exception.domain.UsernameExistException;
import com.m0schy.service.UserService;
import com.m0schy.utility.JWTTokenProvider;


@RestController
@RequestMapping(path = {"/", "/user"})
public class UserResource extends ExceptionHandling{
	
	@Autowired
	private UserService userService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JWTTokenProvider jWTTokenProvider;
	
	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody User user) {
		authenticate(user.getUsername(), user.getPassword());
		User loginUser = userService.findByUsername(user.getUsername());
		UserPrincipal principal = new UserPrincipal(loginUser);
		HttpHeaders jwtHeaders = getJwtHeader(principal);
		return new ResponseEntity<>(loginUser, jwtHeaders, HttpStatus.OK);
	}
	
	@PostMapping("/register")
	public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
		User loginUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
		return new ResponseEntity<>(loginUser, HttpStatus.OK);
	}
	
	private HttpHeaders getJwtHeader(UserPrincipal principal) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(SecurityConstants.JWT_TOKEN_HEADER, jWTTokenProvider.generateJwtToken(principal));
		return headers;
	}

	private void authenticate(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}
	
}
