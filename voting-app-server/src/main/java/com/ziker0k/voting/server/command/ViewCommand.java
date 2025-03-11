package com.ziker0k.voting.server.command;

import com.ziker0k.voting.common.dto.TopicDto;
import com.ziker0k.voting.common.dto.VoteDto;
import com.ziker0k.voting.common.service.VotingService;
import com.ziker0k.voting.server.model.ParsedCommand;
import com.ziker0k.voting.server.model.UserSession;
import com.ziker0k.voting.server.util.ArgumentParser;
import com.ziker0k.voting.server.util.ViewFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(ViewCommand.class);

    private static final String ERROR_MISSING_TOPIC = "Error! Enter topic as parameter -t=<topic>\n";
    private static final String ERROR_TOPIC_NOT_FOUND = "Ошибка! Раздел \"%s\" не найден.\n";
    private static final String ERROR_VOTE_NOT_FOUND = "Ошибка! Голосование \"%s\" не найдено в разделе \"%s\".\n";

    @Override
    public String getName() {
        return "view";
    }

    @Override
    public void execute(UserSession userSession, VotingService votingService, String args) {
        ParsedCommand parsedCommand = ArgumentParser.parse(args);

        if (parsedCommand == null) {
            // Показываем список всех разделов
            log.info("Showing all topics.");
            userSession.getChannelHandlerContext().writeAndFlush(ViewFormatter.formatTopics(votingService.getAllTopics()));
            return;
        }

        String topicName = parsedCommand.getTopic();

        if (topicName.isBlank()) {
            log.warn("Topic name is blank in 'view' command.");
            userSession.getChannelHandlerContext().writeAndFlush(ERROR_MISSING_TOPIC);
            return;
        }

        TopicDto topicDto = votingService.getTopic(topicName);
        if (topicDto == null) {
            log.warn("Topic not found: {}", topicName);
            userSession.getChannelHandlerContext().writeAndFlush(String.format(ERROR_TOPIC_NOT_FOUND, topicName));
            return;
        }

        String voteTitle = parsedCommand.getVote();
        if (voteTitle == null) {
            // Показываем голосования в разделе
            log.info("Showing all votes in topic: {}", topicName);
            userSession.getChannelHandlerContext().writeAndFlush(ViewFormatter.formatVotes(topicDto));
            return;
        }

        VoteDto voteDto = topicDto.getVotes().get(voteTitle);
        if (voteDto == null) {
            log.warn("Vote not found in topic '{}': {}", topicName, voteTitle);
            userSession.getChannelHandlerContext().writeAndFlush(String.format(ERROR_VOTE_NOT_FOUND, voteTitle, topicName));
            return;
        }

        // Показываем детали голосования
        log.info("Showing details of vote '{}' in topic '{}'.", voteTitle, topicName);
        userSession.getChannelHandlerContext().writeAndFlush(ViewFormatter.formatVoteDetails(voteDto));
    }
}