package com.cinnox.bot.controller;

import com.cinnox.bot.transport.bot.BroadCastRequest;
import com.cinnox.bot.transport.bot.BroadcastResponse;
import com.cinnox.bot.transport.bot.UserMessageResponse;
import com.cinnox.bot.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class BotController {
    @Autowired
    private BotService botService;

    @GetMapping("/message/{userId}")
    ResponseEntity<Object> messages(@PathVariable String userId) {
        UserMessageResponse res = new UserMessageResponse();
        List<String> messages = botService.getMessageByUserId(userId);
        res.setUserMessages(messages);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/message/broadcast")
    ResponseEntity<Object> broadcast(@Valid @RequestBody BroadCastRequest payload) {
        int sentCount = botService.broadcast(payload.getMessage());
        BroadcastResponse res = new BroadcastResponse();
        res.setResult("sent " + sentCount + " user(s)");
        return new ResponseEntity<>(res, HttpStatus.OK);

    }
}
