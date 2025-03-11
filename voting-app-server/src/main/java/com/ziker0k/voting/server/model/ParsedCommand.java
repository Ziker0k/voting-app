package com.ziker0k.voting.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@AllArgsConstructor
@Getter
@Value
public class ParsedCommand {
    String topic;
    String vote;
}
