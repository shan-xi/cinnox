package com.cinnox.bot.transport.bot;

import javax.validation.constraints.NotBlank;

public class BroadCastRequest {
    @NotBlank(message = "message is mandatory")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
