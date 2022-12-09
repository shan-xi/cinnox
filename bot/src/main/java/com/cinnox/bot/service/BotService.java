package com.cinnox.bot.service;

import com.cinnox.bot.BotApplication;
import com.cinnox.bot.model.UserInfo;
import com.cinnox.bot.model.UserMessage;
import com.cinnox.bot.repository.UserInfoRepository;
import com.cinnox.bot.repository.UserMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.Future;

@Service
public class BotService {

    @Value("${line.bot.channel-token}")
    private String channelAccessToken;

    @Value("${line.message.user.profile.url}")
    private String profileUrl;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private UserMessageRepository userMessageRepository;

    private final Logger log = LoggerFactory.getLogger(BotApplication.class);

    @Async("threadPoolTaskExecutor")
    public Future<String> saveUserInfoAndMessage(String userId, String message) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<UserInfo> response = restTemplate.exchange(
                    RequestEntity.get(new URI(profileUrl + userId)).headers(requestLineApiHeader()).build(),
                    UserInfo.class);
            if (response.getBody() != null) {
                UserInfo ui = response.getBody();
                Optional<UserInfo> checkUserInfoExist = userInfoRepository.findOneByUserId(ui.getUserId());
                if (checkUserInfoExist.isEmpty()) {
                    userInfoRepository.save(ui);
                }
                UserMessage um = new UserMessage();
                um.setUserId(ui.getUserId());
                um.setMessage(message);
                userMessageRepository.save(um);
            }
            return new AsyncResult<String>("done");
        } catch (Exception e) {
            log.error(e.getMessage());
            return new AsyncResult<String>(e.getMessage());
        }
    }
    private HttpHeaders requestLineApiHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.channelAccessToken);
        return headers;
    }
}
