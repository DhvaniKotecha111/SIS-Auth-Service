package com.opl.utils;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opl.entities.UserCredentials;
import com.opl.proxies.UserCredentialsDto;
import com.opl.repositories.AuthRepo;
import com.opl.services.EmailService;

@Component
public class Helper {

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private AuthRepo authRepo;
	
	public UserCredentials convertDtoToEntity(UserCredentialsDto userCredentialsDto)
	{
		return mapper.convertValue(userCredentialsDto, UserCredentials.class);
	}
	
	public UserCredentialsDto convertEntityToDto(UserCredentials userCredentials)
	{
		return mapper.convertValue(userCredentials, UserCredentialsDto.class);
	}
	
	public String generateOtp()
	{
		Random random = new Random();
		int randomNumber = random.nextInt(999999);
		String output = Integer.toString(randomNumber);
		while (output.length() < 6) {
			output = "0" + output;
		}
		return output;
	}
	
	public String generateToken()
	{
		String ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	    SecureRandom random = new SecureRandom();
	    
	    StringBuilder token = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            int randomIndex = random.nextInt(ALPHANUMERIC.length());
            token.append(ALPHANUMERIC.charAt(randomIndex));
        }
        return token.toString();
	}
	
	public String sendOtpToMail(String email, String otp) throws MessagingException, IOException
	{
		String subject = "OTP Verification";
		return emailService.sendOtpEmail(email, subject, otp);
	}
	
	public String sendLinkToMail(String email, String link) throws MessagingException, IOException {
		String subject = "Reset Password";
		return emailService.sendResetLink(email, subject, link);
	}

	public Boolean checkExpiryOfOtp(String otp, Date createdTime) {
		Timestamp timestamp1 = new Timestamp(createdTime.getTime());
		Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
		Long res = (timestamp2.getTime() - timestamp1.getTime()) / (60 * 1000);
		if(res >= 2) {
			return true;
		}
		return false;
	}
}
