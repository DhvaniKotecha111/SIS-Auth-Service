package com.opl.serviceImpl;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.opl.entities.UserCredentials;
import com.opl.enums.Roles;
import com.opl.forgotPasswordReqAndRes.ForgotPasswordRequest;
import com.opl.forgotPasswordReqAndRes.ResetPasswordRequest;
import com.opl.jwtreqres.JwtRequest;
import com.opl.jwtreqres.JwtResponse;
import com.opl.proxies.UserCredentialsDto;
import com.opl.repositories.AuthRepo;
import com.opl.services.AuthServices;
import com.opl.utils.Helper;
import com.opl.utils.JwtUtils;

@Component
public class AuthServiceImpl implements AuthServices {

	@Autowired
	private AuthRepo authRepo;

	@Autowired
	private Helper helper;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private MyUserDetailsService userDetailsService;

	@Override
	public String studentRegister(UserCredentialsDto userCredentialsDto) {
		try {
			UserCredentials userCredentials = helper.convertDtoToEntity(userCredentialsDto);
			Optional<UserCredentials> byId = authRepo.findById(userCredentials.getUserid());
			if (byId.isPresent()) {
				return "Student is already Registered";
			}
			userCredentials.setRole(Roles.ROLE_USER.toString());
			authRepo.save(userCredentials);
			return "Student Registered Successfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error in student registration";
		}
	}

	@Override
	public String adminRegister(UserCredentialsDto userCredentialsDto) {
		try {
			UserCredentials userCredentials = helper.convertDtoToEntity(userCredentialsDto);
			Optional<UserCredentials> byId = authRepo.findById(userCredentials.getUserid());
			if (byId.isPresent()) {
				return "Admin is already Registered";
			}
			userCredentials.setRole(Roles.ROLE_ADMIN.toString());
			authRepo.save(userCredentials);
			return "Admin Registered Successfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error in admin registration";
		}
	}

	@Override
	public JwtResponse validateAdmin(JwtRequest jwtRequest) {
		userDetailsService.setRole(jwtRequest.getRole());
		return validate(jwtRequest);
	}

	@Override
	public JwtResponse validateStudent(JwtRequest jwtRequest) {
		userDetailsService.setRole(jwtRequest.getRole());
		return validate(jwtRequest);
	}

	private JwtResponse validate(JwtRequest jwtRequest) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));

			UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());

			String token = jwtUtils.generateToken(userDetails);
			return new JwtResponse(token);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public UserCredentialsDto getUserDetails(String name) {
		Optional<UserCredentials> byName = authRepo.findByName(name);
		if (byName.isPresent()) {
			UserCredentials userCredentials = byName.get();
			UserCredentialsDto userCredentialsDto = helper.convertEntityToDto(userCredentials);
			return userCredentialsDto;
		}
		return null;
	}

	@Override
	public String sendOtpToMail(ForgotPasswordRequest req) {
		Optional<UserCredentials> byEmailAndDob = authRepo.findByEmailAndDob(req.getEmail(), req.getDob());
		if (byEmailAndDob.isPresent()) {
			try {
				String otp = helper.generateOtp();
				UserCredentials userCredentials = byEmailAndDob.get();
				userCredentials.setOtp(otp);
				userCredentials.setCreatedTime(new Date());
				authRepo.save(userCredentials);
				String response = helper.sendOtpToMail(req.getEmail(), otp);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String sendLinkToMail(ForgotPasswordRequest req) {
		String baseUrl = "http://localhost:4200/reset-password";
		Optional<UserCredentials> byEmailAndDob = authRepo.findByEmailAndDob(req.getEmail(), req.getDob());
		if (byEmailAndDob.isPresent()) {
			try {
				UserCredentials userCredentials = byEmailAndDob.get();
				String token = helper.generateToken();
				String finalUrl = baseUrl + "?token=" + token + "&email=" + req.getEmail() + "&dob=" + req.getDob();
				userCredentials.setResetPasswordToken(token);
				authRepo.save(userCredentials);
				String response = helper.sendLinkToMail(userCredentials.getEmail(), finalUrl);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String verifyOtp(ForgotPasswordRequest req) {
		Optional<UserCredentials> byEmailAndDobAndOtp = authRepo.findByEmailAndDobAndOtp(req.getEmail(), req.getDob(),
				req.getOtp());
		if (byEmailAndDobAndOtp.isPresent()) {
			UserCredentials userCredentials = byEmailAndDobAndOtp.get();
			Boolean expiryOfOtp = helper.checkExpiryOfOtp(userCredentials.getOtp(),
					userCredentials.getCreatedTime());
			if (expiryOfOtp) {
				userCredentials.setOtp(null);
				userCredentials.setCreatedTime(null);
				authRepo.save(userCredentials);
				return "OTP is expired!! Please generate it again";
			}
			return "OTP Verified Successfully";
		}
		return null;
	}

	@Override
	public String resetPassword(ResetPasswordRequest request) {
		Optional<UserCredentials> byEmailAndDobAndResetPasswordToken = authRepo
				.findByEmailAndDobAndResetPasswordToken(request.getEmail(), request.getDob(), request.getToken());
		if (byEmailAndDobAndResetPasswordToken.isPresent()) {
			UserCredentials userCredentials = byEmailAndDobAndResetPasswordToken.get();

			if (!request.getNewPwd().equals(userCredentials.getPassword())) {
				String[] resetPasswordTokens = userCredentials.getResetPasswordTokens();
				
				// Initialize the array if it's null
				if (resetPasswordTokens == null) {
					resetPasswordTokens = new String[3];
					// Fill the array with empty strings
					Arrays.fill(resetPasswordTokens, "");
				}

				// Shift the passwords to the left
				for (int i = resetPasswordTokens.length - 1; i > 0; i--) {
					resetPasswordTokens[i] = resetPasswordTokens[i - 1];
				}
				
				// Store the current password in the second position
	            resetPasswordTokens[1] = userCredentials.getPassword();
				
				// Set the new password as the first element
	            resetPasswordTokens[0] = request.getNewPwd();

				userCredentials.setResetPasswordTokens(resetPasswordTokens);
				userCredentials.setPassword(request.getNewPwd());
				authRepo.save(userCredentials);
				
				for (String string : resetPasswordTokens) {
					System.err.println(string);
				}
				userCredentials.setResetPasswordToken(null);
				authRepo.save(userCredentials);
				return "Your password is changed Successfully";
			} else {
				return "New password must be different from the current password";
			}
		}
		return null;
	}

	public UserCredentials findByName(String username) {
		Optional<UserCredentials> byName = authRepo.findByName(username);
		if (byName.isPresent()) {
			UserCredentials userCredentials = byName.get();
			return userCredentials;
		}
		return null;
	}
}
