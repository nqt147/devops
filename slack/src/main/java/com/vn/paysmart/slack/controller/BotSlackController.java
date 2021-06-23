package com.vn.paysmart.slack.controller;

import com.google.gson.Gson;
import me.ramswaroop.jbot.core.common.Controller;
import me.ramswaroop.jbot.core.common.JBot;
import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.models.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.WebSocketSession;

@JBot
@Profile("slack")
public class BotSlackController extends Bot {

    public static Gson gson = new Gson();

    @Value("${slackBotToken}")
    private String slackToken;

    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

    @Controller(pattern = "(list action)")
    public void list(WebSocketSession session, Event event) {
        reply(session, event, "1 2 3");
    }

}
