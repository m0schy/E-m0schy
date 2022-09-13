package com.m0schy.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.m0schy.constants.SecurityConstants;
import com.m0schy.filter.JWTAccessDeniedHandler;
import com.m0schy.filter.JWTAuthenticationEntryPoint;
import com.m0schy.filter.JWTAuthorizationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

	private JWTAuthenticationEntryPoint jWTAuthenticationEntryPoint; 
	private JWTAuthorizationFilter jWTAuthorizationFilter; 
	private JWTAccessDeniedHandler jWTAccessDeniedHandler;
	private UserDetailsService userDetailsService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
    public SecurityConfiguration(JWTAuthenticationEntryPoint jWTAuthenticationEntryPoint,
			JWTAuthorizationFilter jWTAuthorizationFilter, JWTAccessDeniedHandler jWTAccessDeniedHandler,
			@Qualifier("UserDetailsService") UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		super();
		this.jWTAuthenticationEntryPoint = jWTAuthenticationEntryPoint;
		this.jWTAuthorizationFilter = jWTAuthorizationFilter;
		this.jWTAccessDeniedHandler = jWTAccessDeniedHandler;
		this.userDetailsService = userDetailsService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Bean
	public AuthenticationManager authenticationManager(
	        AuthenticationConfiguration authConfig) throws Exception {
	    return authConfig.getAuthenticationManager();
	}

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider())
        	.csrf().disable()
        	.cors()
        	.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        	.and().authorizeRequests().antMatchers(SecurityConstants.Public_URLS).permitAll()
        	.anyRequest().authenticated()
        	.and().exceptionHandling().accessDeniedHandler(jWTAccessDeniedHandler)
        	.authenticationEntryPoint(jWTAuthenticationEntryPoint)
        	.and().addFilterBefore(jWTAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(bCryptPasswordEncoder);
		return authProvider;
	}
	
}
