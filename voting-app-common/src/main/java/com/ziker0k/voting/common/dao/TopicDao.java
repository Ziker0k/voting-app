package com.ziker0k.voting.common.dao;

import com.ziker0k.voting.common.entity.Topic;
import com.ziker0k.voting.common.entity.Vote;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TopicDao {
    private static final TopicDao INSTANCE = new TopicDao();

    private final Map<Topic, Map<String, Vote>> votes = new ConcurrentHashMap<>();

    public static TopicDao getInstance() {
        return INSTANCE;
    }

    public Optional<Vote> addVote(Topic topic, Vote vote) {
        Map<String, Vote> topicVotes = votes.computeIfAbsent(topic, k -> new ConcurrentHashMap<>());

        if (topicVotes.containsKey(vote.getVoteTitle())) {
            return Optional.empty();
        }

        topicVotes.put(vote.getVoteTitle(), vote);
        return Optional.of(votes.get(topic).get(vote.getVoteTitle()));
    }

    public boolean removeVote(Topic topic, Vote vote) {
        Map<String, Vote> topicVotes = votes.get(topic);
        if (topicVotes == null) {
            return false;
        }

        if (topicVotes.containsKey(vote.getVoteTitle())) {
            topicVotes.remove(vote.getVoteTitle());
            return true;
        } else {
            return false;
        }
    }

    public Map<String, Vote> getVotes(Topic topic) {
        Map<String, Vote> topicVotes = votes.get(topic);
        if (topicVotes == null) {
            return new HashMap<>();
        }
        return new HashMap<>(topicVotes);
    }

    public void clear() {
        votes.clear();
    }
}
