package com.ziker0k.voting.common.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DoVoteDto {
    String topicName;
    String voteName;
    String voter;
    String option;
}
