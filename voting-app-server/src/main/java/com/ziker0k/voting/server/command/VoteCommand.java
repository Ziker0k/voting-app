package com.ziker0k.voting.server.command;

import com.ziker0k.voting.common.dto.DoVoteDto;
import com.ziker0k.voting.common.dto.TopicDto;
import com.ziker0k.voting.common.dto.VoteDto;
import com.ziker0k.voting.common.service.VotingService;
import com.ziker0k.voting.server.model.ParsedCommand;
import com.ziker0k.voting.server.model.UserSession;
import com.ziker0k.voting.server.util.ArgumentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class VoteCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(VoteCommand.class);

    private static final String ERROR_INVALID_FORMAT = "Invalid command format. Use: vote -t=<topic> -v=<vote>\n";
    private static final String ERROR_TOPIC_NOT_FOUND = "Topic \"%s\" not found.\n";
    private static final String ERROR_VOTE_NOT_FOUND = "Vote \"%s\" not found in topic \"%s\".\n";
    private static final String ERROR_ALREADY_VOTED = "You already voted in vote \"%s\".\n";
    private static final String ERROR_NO_OPTIONS = "This vote has no options available.\n";

    @Override
    public String getName() {
        return "vote";
    }

    @Override
    public void execute(UserSession userSession, VotingService votingService, String args) {
        ParsedCommand parsedCommand = ArgumentParser.parse(args);

        // Validate command format
        if (parsedCommand == null || parsedCommand.getTopic().isBlank() || parsedCommand.getVote().isBlank()) {
            log.warn("Invalid command format: {}", args);
            userSession.getChannelHandlerContext().writeAndFlush(ERROR_INVALID_FORMAT);
            return;
        }

        String topicName = parsedCommand.getTopic();
        String voteTitle = parsedCommand.getVote();

        // Check if topic exists
        TopicDto topicDto = votingService.getTopic(topicName);
        if (topicDto == null) {
            log.warn("Topic not found: {}", topicName);
            userSession.getChannelHandlerContext().writeAndFlush(String.format(ERROR_TOPIC_NOT_FOUND, topicName));
            return;
        }

        // Check if vote exists within the topic
        VoteDto voteDto = topicDto.getVotes().get(voteTitle);
        if (voteDto == null) {
            log.warn("Vote not found: {} in topic: {}", voteTitle, topicName);
            userSession.getChannelHandlerContext().writeAndFlush(String.format(ERROR_VOTE_NOT_FOUND, voteTitle, topicName));
            return;
        }

        // Check if the user has already voted
        if (voteDto.getVoters().contains(userSession.getUsername())) {
            log.info("User {} has already voted in vote: {}", userSession.getUsername(), voteTitle);
            userSession.getChannelHandlerContext().writeAndFlush(String.format(ERROR_ALREADY_VOTED, voteTitle));
            return;
        }

        // Get available options for voting
        Map<String, Integer> options = voteDto.getOptions();
        if (options.isEmpty()) {
            log.warn("Vote {} has no options available.", voteTitle);
            userSession.getChannelHandlerContext().writeAndFlush(ERROR_NO_OPTIONS);
            return;
        }

        // Send voting options to the user
        StringBuilder sb = new StringBuilder("Vote: " + voteDto.getVoteTitle() + "\n");
        sb.append("Choose an option:\n");
        List<String> optionKeys = options.keySet().stream().toList();
        for (int i = 0; i < optionKeys.size(); i++) {
            String option = optionKeys.get(i);
            sb.append(String.format(" %d. %s (%d votes)\n", i + 1, option, options.get(option)));
        }
        sb.append("\nEnter the number of your choice:");

        userSession.getChannelHandlerContext().writeAndFlush(sb.toString());

        // Prepare the vote action DTO
        DoVoteDto doVoteDto = DoVoteDto.builder()
                .topicName(topicName)
                .voteName(voteTitle)
                .build();

        // Track the active vote for the user
        userSession.activeVote(optionKeys, doVoteDto);

        // Log the action
        log.info("User {} is voting in topic: {} for vote: {}", userSession.getUsername(), topicName, voteTitle);
    }
}