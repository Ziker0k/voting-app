package com.ziker0k.voting.common.mapper;

import com.ziker0k.voting.common.dto.TopicDto;
import com.ziker0k.voting.common.dto.VoteDto;
import com.ziker0k.voting.common.entity.Topic;
import com.ziker0k.voting.common.entity.Vote;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TopicMapperTest {
    private final TopicMapper topicMapper = TopicMapper.getInstance();

    @Test
    void map() {
        Vote vote1 = createVote();
        Topic topic = Topic.builder()
                .topicName("test")
                .votes(Map.of(
                        vote1.getVoteTitle(), vote1
                ))
                .build();

        TopicDto actual = topicMapper.map(topic);

        VoteDto expectedVoteDto = createExpectedVoteDto();
        TopicDto expected = TopicDto.builder()
                .topicTitle("test")
                .votes(Map.of(
                        expectedVoteDto.getVoteTitle(), expectedVoteDto
                ))
                .build();
        assertEquals(expected, actual);
    }


    private Vote createVote() {
        return Vote.builder()
                .voteTitle("vote1")
                .creator("Creator")
                .description("Description")
                .options(Map.of("yes", 0, "no", 0))
                .voters(new HashSet<>())
                .build();

    }

    private VoteDto createExpectedVoteDto() {
        return VoteDto.builder()
                .voteTitle("vote1")
                .creator("Creator")
                .description("Description")
                .options(Map.of("yes", 0, "no", 0))
                .voters(new HashSet<>())
                .build();
    }
}