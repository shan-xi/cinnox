package com.cinnox.bot.service;

import com.cinnox.bot.BotApplication;
import com.cinnox.bot.transport.line.MulticastRequest;
import com.cinnox.bot.transport.line.Message;
import com.cinnox.bot.model.UserInfo;
import com.cinnox.bot.model.UserMessage;
import com.cinnox.bot.repository.UserInfoRepository;
import com.cinnox.bot.repository.UserMessageRepository;
import com.google.gson.Gson;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

@Service
public class BotService {

    @Value("${line.bot.channel-token}")
    private String channelAccessToken;

    @Value("${line.message.user.profile.url}")
    private String profileUrl;

    @Value("${line.message.multicast.url}")
    private String multicastUrl;

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

    public int broadcast(String message) {
        List<String> userIds = userInfoRepository.findDistinctUserIds();
        if (userIds.size() > 0) {
            HttpEntity<String> entity = new HttpEntity<String>(multicastRequestJsonString(userIds, message), requestLineApiHeader());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject(multicastUrl, entity, String.class);
            return userIds.size();
        } else {
            return 0;
        }
    }

    private HttpHeaders requestLineApiHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.channelAccessToken);
        return headers;
    }

    private String multicastRequestJsonString(List<String> userIds, String message) {
        MulticastRequest mcreq = new MulticastRequest();
        mcreq.setTo(userIds);
        Message m = new Message();
        m.setType("text");
        m.setText(message);
        List<Message> ms = new ArrayList<>();
        ms.add(m);
        mcreq.setMessages(ms);
        return new Gson().toJson(mcreq);
    }
}
