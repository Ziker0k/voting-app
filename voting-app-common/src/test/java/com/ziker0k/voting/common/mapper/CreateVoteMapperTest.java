package com.ziker0k.voting.common.mapper;

import com.ziker0k.voting.common.dto.CreateVoteDto;
import com.ziker0k.voting.common.entity.Vote;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateVoteMapperTest {
    private final CreateVoteMapper createVoteMapper = CreateVoteMapper.getInstance();

    @Test
    void map() {
        CreateVoteDto createVoteDto = CreateVoteDto.builder()
                .voteTitle("Vote Title")
                .creator("Creator")
                .description("Description")
                .options(Set.of("yes", "no"))
                .build();

        Vote actual = createVoteMapper.map(createVoteDto);

        Vote expected = Vote.builder()
                .voteTitle("Vote Title")
                .creator("Creator")
                .description("Description")
                .options(Map.of("yes", 0, "no", 0))
                .voters(new HashSet<>())
                .build();
        assertEquals(expected, actual);
    }
}