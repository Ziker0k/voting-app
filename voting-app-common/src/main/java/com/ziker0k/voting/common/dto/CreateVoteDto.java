package com.ziker0k.voting.common.dto;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class CreateVoteDto {
    String topicTitle;
    String voteTitle;
    String creator;
    String description;
    Set<String> options;
}
