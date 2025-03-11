package com.ziker0k.voting.server.command;

import com.ziker0k.voting.common.service.VotingService;
import com.ziker0k.voting.server.handler.CreateVoteHandler;
import com.ziker0k.voting.server.model.ParsedCommand;
import com.ziker0k.voting.server.model.UserSession;
import com.ziker0k.voting.server.model.UserState;
import com.ziker0k.voting.server.util.ArgumentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateVoteCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(CreateVoteCommand.class);

    private static final String ERROR_INVALID_ARGUMENTS = "Error! Enter: create vote -t=<topic>\n";
    private static final String ERROR_EMPTY_TOPIC = "Error! Topic shouldn't be empty.\n";
    private static final String ERROR_TOPIC_NOT_EXISTS = "Error! Topic does not exist.\n";
    private static final String SUCCESS_CREATE_VOTE = "Creating vote in \"%s\". Please, type 'enter' to start.\n";

    @Override
    public String getName() {
        return "create vote";
    }

    @Override
    public void execute(UserSession userSession, VotingService votingService, String args) {
        ParsedCommand parsedCommand = ArgumentParser.parse(args);
        if (parsedCommand == null) {
            log.warn("Invalid arguments for creating vote: {}", args);
            userSession.getChannelHandlerContext().writeAndFlush(ERROR_INVALID_ARGUMENTS);
            return;
        }

        String topicName = parsedCommand.getTopic();
        if (topicName.isBlank()) {
            log.warn("Attempted to create vote in an empty topic.");
            userSession.getChannelHandlerContext().writeAndFlush(ERROR_EMPTY_TOPIC);
            return;
        }

        if (votingService.isTopicNotExists(topicName)) {
            log.warn("Topic does not exist: {}", topicName);
            userSession.getChannelHandlerContext().writeAndFlush(ERROR_TOPIC_NOT_EXISTS);
            return;
        }

        // Set user state to 'CREATING_VOTE' and add the handler to the pipeline
        userSession.setState(UserState.CREATING_VOTE);
        userSession.getChannelHandlerContext().channel().pipeline().addLast(new CreateVoteHandler(userSession, votingService, topicName));

        // Send success message
        String successMessage = String.format(SUCCESS_CREATE_VOTE, topicName);
        log.info("User {} started creating a vote in topic: {}", userSession.getUsername(), topicName);
        userSession.getChannelHandlerContext().writeAndFlush(successMessage);
    }
}