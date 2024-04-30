package com.opl.entities;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentials {

	@Id
	@Column(nullable = false, unique = true)
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
