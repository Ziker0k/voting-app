package com.ziker0k.voting.common.dao;

import com.ziker0k.voting.common.entity.Topic;
import com.ziker0k.voting.common.entity.Vote;
import com.ziker0k.voting.common.exception.TopicDaoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TopicDaoTest {
    TopicDao topicDao = TopicDao.getInstance();

    @BeforeEach
    void setUp() {
        topicDao.clear();
    }

    @Test
    void addVote_WhenTopicExistsNoVotes_Success() {
        Topic topic = createTopic("Test Topic");
        Vote vote = Vote.builder()
                .voteTitle("TestVote")
                .build();

        Optional<Vote> actual = topicDao.addVote(topic, vote);

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(vote);
    }

    @Test
    void addVote_WhenDuplicateVote_ShouldThrowException() {
        Topic topic = createTopic("TestTopic");
        Vote vote = createVote("TestVote");
        topicDao.addVote(topic, vote);

        Optional<Vote> actual = topicDao.addVote(topic, vote);

        assertThat(actual).isEmpty();
    }

    @Test
    void removeVote_WhenTopicExistsVoteExists_Success() {
        Topic topic = createTopic("TestTopic");
        Vote vote = createVote("TestVote");
        topicDao.addVote(topic, vote);

        boolean actual = topicDao.removeVote(topic, vote);

        assertTrue(actual);
        assertThat(topicDao.getVotes(topic)).isEmpty();
    }

    @Test
    void removeVote_WhenTopicDoesNotExist_ReturnFalse() {
        boolean actual = topicDao.removeVote(createTopic("TestTopic"), createVote("TestVote"));

        assertFalse(actual);
    }

    @Test
    void removeVote_WhenTopicExistsVoteDoesNotExist_ReturnFalse() {
        Topic topic = createTopic("TestTopic");
        Vote vote = createVote("TestVote");
        Vote voteToDelete = createVote("TestVoteToDelete");
        topicDao.addVote(topic, vote);

        boolean actual = topicDao.removeVote(topic, voteToDelete);

        assertFalse(actual);
    }

    @Test
    void getVotes_WhenTopicNotExists_ReturnEmptyMap() {
        Map<String, Vote> actual = topicDao.getVotes(createTopic("TestTopic"));
        assertThat(actual).isEmpty();
    }

    private Topic createTopic(String topicTitle) {
        return Topic.builder()
                .topicName(topicTitle)
                .votes(new HashMap<>())
                .build();
    }

    private Vote createVote(String voteTitle) {
        return Vote.builder()
                .voteTitle(voteTitle)
                .build();
    }
}