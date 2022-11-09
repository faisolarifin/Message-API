package com.jatismobile.messagesapi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.jatismobile.messagesapi.repository.TokenRepository;
import com.jatismobile.messagesapi.service.Producer;
import com.jatismobile.messagesapi.utils.AppLog;
import com.jatismobile.messagesapi.model.Token;

@RestController
public class MessagesController {

	@Autowired
	Producer producer;
	
	@Autowired
	TokenRepository tokenRepository;
	
	public Map<String, Object> response;
	
	
	@PostMapping("/messages")
	public ResponseEntity<Object> messageResponse(@RequestHeader(value="Authorization") String autorization,
			@RequestBody Map<String, Object> msgPayload) {
		
		try {
			response = new HashMap<>();
			AppLog.logInfo("[Message][payload] receive payload object " + msgPayload.toString());
			@SuppressWarnings("unchecked")
			Map<String, Object> textBody = (Map) msgPayload.get("text");			
			
			String token = autorization.substring(7, autorization.length());
			List<Token> checkToken = tokenRepository.findByToken(token);
			
			AppLog.logInfo("[Database][get][token] list token from database " + checkToken.toString());
			
			//logic 1 check token
			if (checkToken.size() == 0) {
				response.clear();
				response.put("message", "Invalid Access Token!");
				
				return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			}
			//logic 2 check payload
			else if (msgPayload.size() < 5 || textBody.size() < 2 ) {
				
				response.clear();
				
				if (msgPayload.get("messaging_product") == null) response.put("message", "Invalid Payload, required messaging_product attribute!");
				else if (msgPayload.get("recipient_type") == null) response.put("message", "Invalid Payload, required recipient_type attribute!");
				else if (msgPayload.get("to") == null) response.put("message", "Invalid Payload, required to attribute!");
				else if (msgPayload.get("type") == null) response.put("message", "Invalid Payload, required type attribute!");
				else if (msgPayload.get("text") == null) response.put("message", "Invalid Payload, required text attribute!");
				else if (textBody.get("preview_url") == null) response.put("message", "Invalid Payload, required text.preview_url attribute!");
				else if (textBody.get("body") == null) response.put("message", "Invalid Payload, required text.body attribute!");

				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);			
			}
			
			//generate unix message id
			String messageId = UUID.randomUUID().toString();
			//send to message broker activemq 
			producer.sendMessage(messageId, msgPayload);
		
			return new ResponseEntity<>(msgPayload, HttpStatus.OK);
			
		} catch (Exception e) {
			
			AppLog.logInfo("Error : " + e.toString());
			
			response.clear();
			response.put("message", "Server Error!");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	} 
}