package com.ziker0k.voting.server.command;

import com.ziker0k.voting.common.service.VotingService;
import com.ziker0k.voting.server.model.UserSession;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTopicCommand implements Command {

    private static final Logger log = LoggerFactory.getLogger(CreateTopicCommand.class);

    private static final String TOPIC_PREFIX = "-n=";
    private static final String ERROR_INVALID_FORMAT = "Error! Enter: create topic -n=<topic>\n";
    private static final String ERROR_EMPTY_TOPIC = "Error! Topic title shouldn't be empty.\n";
    private static final String TOPIC_CREATED_SUCCESSFULLY = "Topic \"%s\" created successfully.\n";
    private static final String TOPIC_ALREADY_EXISTS = "Topic \"%s\" already exists.\n";

    @Override
    public String getName() {
        return "create topic";
    }

    @Override
    public void execute(UserSession userSession, VotingService votingService, String args) {
        ChannelHandlerContext ctx = userSession.getChannelHandlerContext();

        // Проверяем правильность формата аргумента
        if (!args.startsWith(TOPIC_PREFIX) || args.length() < 4) {
            log.warn("Invalid command format for creating topic: {}", args);
            ctx.writeAndFlush(ERROR_INVALID_FORMAT);
            return;
        }

        String topicName = args.replace(TOPIC_PREFIX, "").trim();

        // Проверяем, что тема не пустая
        if (topicName.isEmpty()) {
            log.warn("Attempted to create a topic with an empty name.");
            ctx.writeAndFlush(ERROR_EMPTY_TOPIC);
            return;
        }

        // Пытаемся создать тему
        boolean created = votingService.createTopic(topicName);
        if (created) {
            log.info("Topic created successfully: {}", topicName);
            ctx.writeAndFlush(String.format(TOPIC_CREATED_SUCCESSFULLY, topicName));
        } else {
            log.warn("Attempted to create an already existing topic: {}", topicName);
            ctx.writeAndFlush(String.format(TOPIC_ALREADY_EXISTS, topicName));
        }
    }
}
