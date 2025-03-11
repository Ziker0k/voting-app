package com.ziker0k.voting.server.util;

import com.ziker0k.voting.common.dto.TopicDto;
import com.ziker0k.voting.common.dto.VoteDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ViewFormatterTest {

    @Test
    void formatTopics_WhenHasTopics_ShouldReturnFormattedTopics() {
        List<TopicDto> topics = List.of(
                generateTopicDto("Topic1"),
                generateTopicDto("Topic2"));

        String actual = ViewFormatter.formatTopics(topics);

        String expected = """
                Available topics:
                 - Topic1 (votes: 3)
                 - Topic2 (votes: 3)
                """;
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void formatTopics_WhenNoTopics_ShouldReturnNoTopicsMessage() {
        List<TopicDto> topics = List.of();

        String actual = ViewFormatter.formatTopics(topics);

        assertEquals("No topics available.\n", actual);
    }

    @Test
    void formatVotes_WhenNoVotes_ShouldReturnNoVotesMessage() {
        TopicDto topic = TopicDto.builder()
                .topicTitle("Technology")
                .votes(Map.of())
                .build();

        String result = ViewFormatter.formatVotes(topic);

        assertEquals("There are no votes in the topic \"Technology\".\n", result);
    }

    @Test
    void formatVotes_WhenHasVotes_ShouldReturnFormattedVoteList() {
        VoteDto vote1 = getVoteDto("AI in 2050", "How will AI evolve?");
        TopicDto topic = getTopicDto("Technology", Map.of(
                vote1.getVoteTitle(), vote1
        ));

        String actual = ViewFormatter.formatVotes(topic);

        String expected = """
                Votes in topic "Technology":
                 - AI in 2050: How will AI evolve?
                """;
        assertEquals(expected, actual);
    }

    @Test
    void formatVotes_WhenOneVote_ShouldReturnSingleVote() {
        VoteDto vote = getVoteDto("Future of Quantum Computing", "What will quantum computers be capable of?");
        TopicDto topic = getTopicDto("Science", Map.of("Future of Quantum Computing", vote));

        String result = ViewFormatter.formatVotes(topic);

        String expected = """
                Votes in topic "Science":
                 - Future of Quantum Computing: What will quantum computers be capable of?
                """;
        assertEquals(expected, result);
    }

    @Test
    void formatVoteDetails_WhenHasVote_ShouldReturnFormattedVoteDetails() {
        VoteDto voteDto = generateVoteDto(1);

        String actual = ViewFormatter.formatVoteDetails(voteDto);

        String expected = """
                Vote: Vote1
                Description: Description1
                Options:
                 - no (votes: 1)
                 - yes (votes: 1)
                """;
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    private static TopicDto generateTopicDto(String topicTitle) {
        return TopicDto.builder()
                .topicTitle(topicTitle)
                .votes(generateMapOfVoteDto())
                .build();
    }

    private static Map<String, VoteDto> generateMapOfVoteDto() {
        Map<String, VoteDto> map = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            VoteDto voteDto = generateVoteDto(i);
            map.put(voteDto.getVoteTitle(), voteDto);
        }
        return map;
    }

    private static VoteDto generateVoteDto(Integer index) {
        return VoteDto.builder()
                .voteTitle("Vote" + index)
                .description("Description" + index)
                .creator("Creator")
                .voters(generateVoters())
                .options(generateOptions())
                .build();
    }

    private static HashSet<String> generateVoters() {
        HashSet<String> voters = new HashSet<>();
        voters.add("Creator");
        voters.add("Voter");
        return voters;
    }

    private static HashMap<String, Integer> generateOptions() {
        HashMap<String, Integer> options = new HashMap<>();
        options.put("no", 1);
        options.put("yes", 1);
        return options;
    }

    private TopicDto getTopicDto(String topicTitle, Map<String, VoteDto> votes) {
        return TopicDto.builder()
                .topicTitle(topicTitle)
                .votes(votes)
                .build();
    }

    private VoteDto getVoteDto(String title, String description) {
        return VoteDto.builder()
                .voteTitle(title)
                .description(description)
                .build();
    }
}