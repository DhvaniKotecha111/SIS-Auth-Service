package com.opl.forgotPasswordReqAndRes;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {

	private String token;
	private String email;
	private LocalDate dob;
	private String newPwd;
}
