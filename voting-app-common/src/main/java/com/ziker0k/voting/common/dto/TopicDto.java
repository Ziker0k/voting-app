package com.ziker0k.voting.common.dto;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class TopicDto {
    String topicTitle;
    Map<String, VoteDto> votes;
}
