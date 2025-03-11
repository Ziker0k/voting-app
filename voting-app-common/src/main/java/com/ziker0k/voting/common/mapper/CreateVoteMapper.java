package com.ziker0k.voting.common.mapper;

import com.ziker0k.voting.common.dto.CreateVoteDto;
import com.ziker0k.voting.common.entity.Vote;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CreateVoteMapper implements Mapper<CreateVoteDto, Vote> {
    private static final CreateVoteMapper INSTANCE = new CreateVoteMapper();

    public static CreateVoteMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public Vote map(CreateVoteDto object) {
        Map<String, Integer> mappedOptions = object.getOptions().stream().collect(Collectors.toMap(Function.identity(), option -> 0));
        return Vote.builder()
                .voteTitle(object.getVoteTitle())
                .creator(object.getCreator())
                .description(object.getDescription())
                .options(mappedOptions)
                .voters(new HashSet<>())
                .build();
    }
}
