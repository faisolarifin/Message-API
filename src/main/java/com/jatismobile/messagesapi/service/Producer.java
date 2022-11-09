package com.jatismobile.messagesapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.jatismobile.messagesapi.utils.AppLog;

import org.springframework.jms.core.MessageCreator;
import javax.jms.Message;
import javax.jms.Session;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;

@Component
public class Producer {

	@Value("${active-mq.topic}")
	String topic;
	
	@Autowired
	JmsTemplate jmsTemplate; 
				
	public void sendMessage(final String messageId, final Map<String, Object> payloadMessage) {
		try {
			AppLog.logInfo("[Queue][send][messageid] sending with message Id " + messageId);
			AppLog.logInfo("[queue][send][payload] sending with payload " + payloadMessage.toString());
			jmsTemplate.send(topic, new MessageCreator() {

				public Message createMessage(Session session) throws JMSException {
					MapMessage message = session.createMapMessage();
					message.setObject("message_id", messageId);
					message.setObject("payload", payloadMessage);
					
					AppLog.logInfo("[Queue][send][object] sending queue message body " + message.toString());
					return message;
				}
				
			});

		} catch (Exception e) {
			
			AppLog.logInfo("Error : " + e.toString());
		}
		
	}

}