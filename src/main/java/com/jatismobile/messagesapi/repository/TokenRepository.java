package com.jatismobile.messagesapi.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jatismobile.messagesapi.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token,Long>{

	List<Token> findByToken(String token);

}