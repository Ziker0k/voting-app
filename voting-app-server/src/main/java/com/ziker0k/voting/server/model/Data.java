package com.ziker0k.voting.server.model;

import com.ziker0k.voting.common.entity.Topic;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Data {
    Map<String, Topic> topics;
}
