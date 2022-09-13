package com.m0schy.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.m0schy.domain.UserPrincipal;
import com.m0schy.service.LoginAttemptService;

@Component
public class AuthenticationSucessListener {

	@Autowired
	private LoginAttemptService loginAttemptService;
	
	@EventListener
	public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
		Object principal = event.getAuthentication().getPrincipal();
		if(principal instanceof UserPrincipal) {
			UserPrincipal user = (UserPrincipal) principal;
			loginAttemptService.evictUserFromLogingAttemptCache(user.getUsername());
		}
	}
}
