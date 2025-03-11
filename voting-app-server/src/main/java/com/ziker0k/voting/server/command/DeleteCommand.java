package com.ziker0k.voting.server.command;

import com.ziker0k.voting.common.dto.TopicDto;
import com.ziker0k.voting.common.dto.VoteDto;
import com.ziker0k.voting.common.service.VotingService;
import com.ziker0k.voting.server.model.ParsedCommand;
import com.ziker0k.voting.server.model.UserSession;
import com.ziker0k.voting.server.util.ArgumentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(DeleteCommand.class);

    // Константы сообщений об ошибках
    private static final String ERROR_INVALID_FORMAT = "Invalid command format. Use: delete -t=<topic> -v=<vote>\n";
    private static final String ERROR_TOPIC_NOT_FOUND = "Topic \"%s\" not found.\n";
    private static final String ERROR_VOTE_NOT_FOUND = "Vote \"%s\" not found in topic \"%s\".\n";
    private static final String ERROR_NOT_CREATOR = "You can only delete votes that you created.\n";
    private static final String SUCCESS_VOTE_DELETED = "Vote \"%s\" has been deleted from topic \"%s\".\n";

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public void execute(UserSession userSession, VotingService votingService, String args) {
        // Парсим команду
        ParsedCommand parsedCommand = ArgumentParser.parse(args);
        if (parsedCommand == null || parsedCommand.getTopic().isBlank() || parsedCommand.getVote().isBlank()) {
            log.warn("Invalid delete command format: {}", args);
            userSession.getChannelHandlerContext().writeAndFlush(ERROR_INVALID_FORMAT);
            return;
        }

        String topicName = parsedCommand.getTopic();
        String voteTitle = parsedCommand.getVote();

        // Проверка существования темы
        TopicDto topic = votingService.getTopic(topicName);
        if (topic == null) {
            log.warn("Topic not found: {}", topicName);
            userSession.getChannelHandlerContext().writeAndFlush(String.format(ERROR_TOPIC_NOT_FOUND, topicName));
            return;
        }

        // Проверка существования голосования в теме
        VoteDto vote = topic.getVotes().get(voteTitle);
        if (vote == null) {
            log.warn("Vote not found: {} in topic: {}", voteTitle, topicName);
            userSession.getChannelHandlerContext().writeAndFlush(String.format(ERROR_VOTE_NOT_FOUND, voteTitle, topicName));
            return;
        }

        // Проверка, является ли пользователь создателем голосования
        if (!vote.getCreator().equals(userSession.getUsername())) {
            log.warn("User {} tried to delete vote they didn't create: {}", userSession.getUsername(), voteTitle);
            userSession.getChannelHandlerContext().writeAndFlush(ERROR_NOT_CREATOR);
            return;
        }

        // Удаление голосования
        votingService.removeVote(voteTitle, topic.getTopicTitle());
        log.info("User {} deleted vote {} in topic {}", userSession.getUsername(), voteTitle, topicName);
        userSession.getChannelHandlerContext().writeAndFlush(String.format(SUCCESS_VOTE_DELETED, voteTitle, topicName));
    }
}