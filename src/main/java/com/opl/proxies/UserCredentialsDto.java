package com.opl.proxies;

import java.time.LocalDate;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialsDto {

	private String userid;
	private String email;
	private String name;
	private LocalDate dob;
	private String password;
	
	// Array to store passwords for reset functionality
    private String[] resetPasswordTokens = new String[3];
    
	private String securityKey;
	private String role;
	private String otp;
	private Date createdTime;
	private String resetPasswordToken;
}
