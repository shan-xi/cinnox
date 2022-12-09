package com.cinnox.bot.transport.bot;

import java.util.List;

public class UserMessageResponse {
    private List<String> userMessages;

    public List<String> getUserMessages() {
        return userMessages;
    }

    public void setUserMessages(List<String> userMessages) {
        this.userMessages = userMessages;
    }
}
