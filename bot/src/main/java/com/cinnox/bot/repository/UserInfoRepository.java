package com.cinnox.bot.repository;

import com.cinnox.bot.model.UserInfo;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInfoRepository extends MongoRepository<UserInfo, String> {
    @Aggregation(pipeline = {"{ '$group': { '_id' : '$userId' } }"})
    public List<String> findDistinctUserIds();

    public Optional<UserInfo> findOneByUserId(String userId);


}