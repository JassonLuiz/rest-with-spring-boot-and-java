package io.github.jassonluiz.restwithspringbootandjava.services;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.jassonluiz.restwithspringbootandjava.dta.vo.v1.security.AccountCredentialsVO;
import io.github.jassonluiz.restwithspringbootandjava.dta.vo.v1.security.TokenVO;
import io.github.jassonluiz.restwithspringbootandjava.repositories.UserRepository;
import io.github.jassonluiz.restwithspringbootandjava.security.Jwt.JwtTokenProvider;

@Service
public class AuthServices {
	
	private Logger logger = Logger.getLogger(AuthServices.class.getName());

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository repository;

	@SuppressWarnings("rawtypes")
	public ResponseEntity signin(AccountCredentialsVO data) {
		try {
			
			logger.info("Entrando no método signin da service...");
			
			var username = data.getUsername();
			var password = data.getPassword();
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

			logger.info("Entrando no método findByUserName do repository...");
			var user = repository.findByUserName(username);

			var tokenResponse = new TokenVO();
			if (user != null) {
				logger.info("Criando o token response.");
				tokenResponse = tokenProvider.createAccessToken(username, user.getRoles());
			} else {
				throw new UsernameNotFoundException("Username " + username + " not found!");
			}
			
			logger.info("Método signin da service concluido.");
			return ResponseEntity.ok(tokenResponse);
		} catch (Exception e) {
			throw new BadCredentialsException("Invalid username/password supplied!");
		}
	}

	@SuppressWarnings("rawtypes")
	public ResponseEntity refreshToken(String username, String refreshToken) {
		var user = repository.findByUserName(username);

		var tokenResponse = new TokenVO();
		if (user != null) {
			tokenResponse = tokenProvider.refreshToken(refreshToken);
		} else {
			throw new UsernameNotFoundException("Username " + username + " not found!");
		}
		return ResponseEntity.ok(tokenResponse);
	}

}
