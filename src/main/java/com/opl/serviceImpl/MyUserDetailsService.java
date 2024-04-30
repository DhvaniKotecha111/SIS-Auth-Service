package com.opl.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.opl.entities.UserCredentials;

@Component
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private AuthServiceImpl authServiceImpl;
	
	@Bean
	public PasswordEncoder encoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	private String role;
	
	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserCredentials userCredentials = authServiceImpl.findByName(username);
		if (userCredentials != null) {
			if (role.equals(userCredentials.getRole())) {
				return new MyUserDetails(userCredentials);
			}
			else
			{
				throw new BadCredentialsException("Invalid Credentials");
			}
		}
		throw new UsernameNotFoundException(username);
	}

}
