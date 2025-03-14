package com.ziker0k.voting.server.model;

import com.ziker0k.voting.common.entity.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Data {
    Map<String, Topic> topics;
}
