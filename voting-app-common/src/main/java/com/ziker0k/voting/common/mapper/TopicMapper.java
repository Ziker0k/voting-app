package com.ziker0k.voting.common.mapper;

import com.ziker0k.voting.common.dto.TopicDto;
import com.ziker0k.voting.common.dto.VoteDto;
import com.ziker0k.voting.common.entity.Topic;
import com.ziker0k.voting.common.entity.Vote;

import java.util.Map;
import java.util.stream.Collectors;

public class TopicMapper implements Mapper<Topic, TopicDto> {
    public static final TopicMapper INSTANCE = new TopicMapper();

    public static TopicMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public TopicDto map(Topic object) {
        return TopicDto.builder()
                .topicTitle(object.getTopicName())
                .votes(toDtoMap(object.getVotes()))
                .build();
    }

    private Map<String, VoteDto> toDtoMap(Map<String, Vote> votes) {
        return votes.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                stringVoteEntry -> buildVoteDto(stringVoteEntry.getValue())
        ));
    }

    private VoteDto buildVoteDto(Vote vote) {
        return VoteDto.builder()
                .voteTitle(vote.getVoteTitle())
                .creator(vote.getCreator())
                .description(vote.getDescription())
                .options(vote.getOptions())
                .voters(vote.getVoters())
                .build();
    }
}