package com.cinnox.bot.transport.line;

import java.util.List;

public class MulticastRequest {
    private List<String> to;
    private List<Message> messages;

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
