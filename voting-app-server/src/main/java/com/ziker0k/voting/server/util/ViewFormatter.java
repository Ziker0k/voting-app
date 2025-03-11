package com.ziker0k.voting.server.util;

import com.ziker0k.voting.common.dto.TopicDto;
import com.ziker0k.voting.common.dto.VoteDto;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class ViewFormatter {
    /**
     * Formats the list of topics.
     *
     * @param topics The map of topics.
     * @return A formatted string.
     */
    public static String formatTopics(List<TopicDto> topics) {
        if (topics.isEmpty()) {
            return "No topics available.\n";
        }

        StringBuilder sb = new StringBuilder("Available topics:\n");
        topics.forEach(topicDto ->
                sb.append(String.format(" - %s (votes: %d)\n", topicDto.getTopicTitle(), topicDto.getVotes().size()))
        );
        return sb.toString();
    }

    /**
     * Formats the list of votes in a topic.
     *
     * @param topic The topic containing votes.
     * @return A formatted string.
     */
    public static String formatVotes(TopicDto topic) {
        if (topic.getVotes().isEmpty()) {
            return "There are no votes in the topic \"" + topic.getTopicTitle() + "\".\n";
        }

        StringBuilder sb = new StringBuilder("Votes in topic \"" + topic.getTopicTitle() + "\":\n");
        for (VoteDto vote : topic.getVotes().values().stream().toList()) {
            sb.append(String.format(" - %s: %s\n", vote.getVoteTitle(), vote.getDescription()));
        }
        return sb.toString();
    }

    /**
     * Formats the details of a specific vote.
     *
     * @param voteDto The vote.
     * @return A formatted string.
     */
    public static String formatVoteDetails(VoteDto voteDto) {
        StringBuilder sb = new StringBuilder("Vote: " + voteDto.getVoteTitle() + "\n");
        sb.append("Description: ").append(voteDto.getDescription()).append("\n");
        sb.append("Options:\n");

        voteDto.getOptions().forEach((option, count) ->
                sb.append(String.format(" - %s (votes: %d)\n", option, count))
        );
        return sb.toString();
    }
}
