package com.cinnox.bot.controller;

import com.cinnox.bot.service.BotService;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;

@LineMessageHandler
public class MessageController {
    @Autowired
    private BotService botService;

    @EventMapping
    public Message handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
        final String messageText = event.getMessage().getText();
        final String userId = event.getSource().getUserId();
        botService.saveUserInfoAndMessage(userId, messageText);
        return new TextMessage("you said: " + messageText);
    }
}
