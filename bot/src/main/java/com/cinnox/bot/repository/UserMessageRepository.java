package com.cinnox.bot.repository;

import com.cinnox.bot.model.UserMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMessageRepository extends MongoRepository<UserMessage, String> {
    public List<UserMessage> findByUserId(String userId);

}
