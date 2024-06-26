package io.github.jassonluiz.restwithspringbootandjava.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.jassonluiz.restwithspringbootandjava.dta.vo.v1.security.AccountCredentialsVO;
import io.github.jassonluiz.restwithspringbootandjava.services.AuthServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Endpoint")
public class AuthController {
	
	private Logger logger = Logger.getLogger(AuthController.class.getName());

	@Autowired
	AuthServices authServices;
	
	@SuppressWarnings("rawtypes")
	@Operation(summary = "Authentication a user and retuns a token")
	@PostMapping(value = "/signin")
	public ResponseEntity signin(@RequestBody AccountCredentialsVO data) {
		
		logger.info("Entrando no método signin da controller...");
		
		if(checkIfParamsIsNotNull(data)) 
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
		var token = authServices.signin(data);
		if(token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
		
		logger.info("Método signin da controller concluido.");
		return token; 
	}
	
	@SuppressWarnings("rawtypes")
	@Operation(summary = "Refresh token for authenticate a user and retuns a token")
	@PutMapping(value = "/refresh/{username}")
	public ResponseEntity refreshToken(@PathVariable("username") String username, 
			@RequestHeader("Authorization") String refreshToken) {
		if(checkIfParamsIsNotNull(username, refreshToken)) 
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
		var token = authServices.refreshToken(username, refreshToken);
		if(token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
		return token; 
	}

	private boolean checkIfParamsIsNotNull(String username, String refreshToken) {
		return refreshToken == null || refreshToken.isBlank() || username == null || username.isBlank();
	}

	private boolean checkIfParamsIsNotNull(AccountCredentialsVO data) {
		return data == null || data.getUsername() == null || data.getUsername().isBlank()
				| data.getPassword() == null || data.getPassword().isBlank();
	}
}
