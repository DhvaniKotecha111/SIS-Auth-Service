package com.opl.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.opl.entities.UserCredentials;

@Repository
public interface AuthRepo extends CrudRepository<UserCredentials, String> {

	Optional<UserCredentials> findByName(String name);
	
	Optional<UserCredentials> findByEmailAndDob(String email, LocalDate dob);
	
	Optional<UserCredentials> findByEmailAndDobAndOtp(String email, LocalDate dob, String otp);
	
	Optional<UserCredentials> findByEmailAndDobAndResetPasswordToken(String email, LocalDate dob, String token);
}
