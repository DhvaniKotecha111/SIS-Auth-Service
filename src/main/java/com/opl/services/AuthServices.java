package com.opl.services;

import org.springframework.stereotype.Service;

import com.opl.forgotPasswordReqAndRes.ForgotPasswordRequest;
import com.opl.forgotPasswordReqAndRes.ResetPasswordRequest;
import com.opl.jwtreqres.JwtRequest;
import com.opl.jwtreqres.JwtResponse;
import com.opl.proxies.UserCredentialsDto;

@Service
public interface AuthServices {

	public String studentRegister(UserCredentialsDto userCredentialsDto);

	public String adminRegister(UserCredentialsDto userCredentialsDto);

	public JwtResponse validateAdmin(JwtRequest jwtRequest);

	public JwtResponse validateStudent(JwtRequest jwtRequest);

	public UserCredentialsDto getUserDetails(String name);
	
	public String sendOtpToMail(ForgotPasswordRequest req);
	
	public String sendLinkToMail(ForgotPasswordRequest req);
	
	public String verifyOtp(ForgotPasswordRequest request);
	
	public String resetPassword(ResetPasswordRequest request);

}
