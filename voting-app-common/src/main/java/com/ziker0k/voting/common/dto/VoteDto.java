package com.ziker0k.voting.common.dto;

import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.Set;

@Value
@Builder
public class VoteDto {
    String voteTitle;
    String creator;
    String description;
    Map<String, Integer> options;
    Set<String> voters;
}
