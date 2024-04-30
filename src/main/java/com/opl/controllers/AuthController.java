package com.opl.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.opl.enums.Roles;
import com.opl.forgotPasswordReqAndRes.ForgotPasswordRequest;
import com.opl.forgotPasswordReqAndRes.ResetPasswordRequest;
import com.opl.jwtreqres.JwtRequest;
import com.opl.jwtreqres.JwtResponse;
import com.opl.proxies.UserCredentialsDto;
import com.opl.services.AuthServices;

@RestController
@CrossOrigin("*")
public class AuthController {

	@Autowired
	private AuthServices authServices;

	@PostMapping("/student/partialRegister")
	public ResponseEntity<?> studentRegister(@RequestBody UserCredentialsDto userCredentialsDto) {
		return new ResponseEntity<String>(authServices.studentRegister(userCredentialsDto), HttpStatus.OK);
	}

	@PostMapping("/admin/partialRegister")
	public String adminRegister(@RequestBody UserCredentialsDto userCredentialsDto) {
		return authServices.adminRegister(userCredentialsDto);
	}

	@PostMapping("/login")
	public JwtResponse loginWithCredentials(@RequestHeader("Role") String role, @RequestBody JwtRequest jwtRequest) {
		if (Roles.ROLE_ADMIN.toString().equals(role)) {
			jwtRequest.setRole(role);
			return authServices.validateAdmin(jwtRequest);
		} else if (role.equals(Roles.ROLE_USER.toString())) {
			jwtRequest.setRole(role);
			return authServices.validateStudent(jwtRequest);
		} else {
			return null;
		}
	}

	@PostMapping("/validate")
	public ResponseEntity<Boolean> validateToken() {
		return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
	}

	@GetMapping("/getUserDetails/{name}")
	public UserCredentialsDto getUserDetails(@PathVariable String name) {
		return authServices.getUserDetails(name);
	}

	@PostMapping("/forgotPassword")
	public String forgotPassword(@RequestBody ForgotPasswordRequest req) {
		return authServices.sendOtpToMail(req);
		//return authServices.sendLinkToMail(req);
	}

	@PostMapping("/verifyOtp")
	public String verifyOtp(@RequestBody ForgotPasswordRequest req) {
		return authServices.verifyOtp(req);
	}
	
	@PostMapping("/resetPassword")
	public String resetPassword(@RequestBody ResetPasswordRequest request) {
		return authServices.resetPassword(request);
	}
}
