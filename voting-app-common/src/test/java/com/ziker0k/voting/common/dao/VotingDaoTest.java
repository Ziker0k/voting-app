package com.ziker0k.voting.common.dao;

import com.ziker0k.voting.common.entity.Topic;
import com.ziker0k.voting.common.entity.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class VotingDaoTest {

    private final VotingDao votingDao = VotingDao.getInstance();

    @BeforeEach
    public void setUp() {
        votingDao.clear();
    }

    @Test
    public void saveTopicSuccessfully() {
        Topic topic = Topic.builder()
                .topicName("Topic1")
                .votes(new HashMap<>())
                .build();

        Optional<Topic> savedTopic = votingDao.save(topic);

        assertTrue(savedTopic.isPresent(), "Топик должен быть сохранен");
        assertEquals(topic, savedTopic.get(), "Сохраненный топик должен быть таким же, как ожидаемый");
    }

    @Test
    public void saveTopicDuplicate() {
        Topic topic = Topic.builder()
                .topicName("Topic1")
                .votes(new HashMap<>())
                .build();
        votingDao.save(topic);

        Optional<Topic> savedTopic = votingDao.save(topic);

        assertTrue(savedTopic.isEmpty(), "Топик не должен быть сохранен дважды");
    }

    @Test
    public void findAllTopics() {
        Topic topic1 = Topic.builder()
                .topicName("Topic1")
                .votes(new HashMap<>())
                .build();
        votingDao.save(topic1);
        Topic topic2 = Topic.builder()
                .topicName("Topic2")
                .votes(new HashMap<>())
                .build();
        votingDao.save(topic2);

        List<Topic> topics = votingDao.findAll();

        assertNotNull(topics, "Список топиков не должен быть null");
        assertThat(topics).hasSize(2);
        assertThat(topics).contains(topic1, topic2);
    }

    @Test
    public void findTopicByName() {
        Topic topic1 = Topic.builder()
                .topicName("Topic1")
                .votes(new HashMap<>())
                .build();
        votingDao.save(topic1);

        Optional<Topic> foundTopic = votingDao.findByName(topic1.getTopicName());

        assertTrue(foundTopic.isPresent(), "Топик должен быть найден");
        assertThat(foundTopic.get()).isEqualTo(topic1);
    }

    @Test
    public void findTopicByNameNotFound() {
        Topic topic1 = Topic.builder()
                .topicName("Topic1")
                .votes(new HashMap<>())
                .build();
        votingDao.save(topic1);
        Topic topic2 = Topic.builder()
                .topicName("Topic2")
                .votes(new HashMap<>())
                .build();
        votingDao.save(topic2);

        // Пытаемся найти топик, который не существует
        Optional<Topic> foundTopic = votingDao.findByName("NonExistingTopic");

        assertTrue(foundTopic.isEmpty(), "Топик не должен быть найден");
    }

    @Test
    public void addVoteToTopicSuccessfully() {
        Topic topic1 = Topic.builder()
                .topicName("Topic1")
                .votes(new HashMap<>())
                .build();
        votingDao.save(topic1);
        Vote vote1 = Vote.builder()
                .voteTitle("Vote1")
                .creator("Creator")
                .description("Desc")
                .options(Map.of("Yes", 0, "No", 0))
                .voters(new HashSet<>())
                .build();

        boolean result = votingDao.addVoteToTopic(vote1, topic1);
        Map<String, Vote> actualVotes = votingDao.findByName(topic1.getTopicName()).get().getVotes();

        assertTrue(result, "Голосование должно быть успешно добавлено в топик");
        assertThat(actualVotes).hasSize(1);
        assertThat(actualVotes).containsEntry("Vote1", vote1);
    }

    @Test
    public void addVoteToTopicWhenTopicNotFound() {
        Topic topic1 = Topic.builder()
                .topicName("Topic1")
                .votes(new HashMap<>())
                .build();
        votingDao.save(topic1);
        Topic topic2 = Topic.builder()
                .topicName("Topic2")
                .votes(new HashMap<>())
                .build();
        Vote vote1 = Vote.builder()
                .voteTitle("Vote1")
                .creator("Creator")
                .description("Desc")
                .options(Map.of("Yes", 0, "No", 0))
                .voters(new HashSet<>())
                .build();

        boolean result = votingDao.addVoteToTopic(vote1, topic2);

        assertFalse(result, "Голосование не должно быть добавлено в несуществующий топик");
    }

    @Test
    public void removeVoteFromTopicSuccessfully() {
        Topic topic1 = Topic.builder()
                .topicName("Topic1")
                .votes(new HashMap<>())
                .build();
        Vote vote1 = Vote.builder()
                .voteTitle("Vote1")
                .creator("Creator")
                .description("Desc")
                .options(Map.of("Yes", 0, "No", 0))
                .voters(new HashSet<>())
                .build();
        votingDao.save(topic1);
        votingDao.addVoteToTopic(vote1, topic1);

        boolean result = votingDao.removeVoteFromTopic(vote1.getVoteTitle(), topic1.getTopicName());

        assertTrue(result, "Голосование должно быть успешно удалено из топика");
        assertThat(votingDao.findByName(topic1.getTopicName()).get().getVotes()).isEmpty();
    }

    @Test
    public void removeVoteFromTopicWhenVoteNotFound() {
        Topic topic1 = Topic.builder()
                .topicName("Topic1")
                .votes(new HashMap<>())
                .build();
        Vote vote1 = Vote.builder()
                .voteTitle("Vote1")
                .creator("Creator")
                .description("Desc")
                .options(Map.of("Yes", 0, "No", 0))
                .voters(new HashSet<>())
                .build();
        votingDao.save(topic1);
        votingDao.addVoteToTopic(vote1, topic1);

        // Пытаемся удалить голосование, которое не существует
        boolean result = votingDao.removeVoteFromTopic("NonExistingVote", topic1.getTopicName());

        assertFalse(result, "Голосование не должно быть удалено, если оно не существует");
        assertThat(votingDao.findByName(topic1.getTopicName()).get().getVotes()).hasSize(1);
    }

    @Test
    public void removeVoteFromTopicWhenTopicNotFound() {
        Topic topic1 = Topic.builder()
                .topicName("Topic1")
                .votes(new HashMap<>())
                .build();
        Vote vote1 = Vote.builder()
                .voteTitle("Vote1")
                .creator("Creator")
                .description("Desc")
                .options(Map.of("Yes", 0, "No", 0))
                .voters(new HashSet<>())
                .build();
        votingDao.save(topic1);
        votingDao.addVoteToTopic(vote1, topic1);

        boolean result = votingDao.removeVoteFromTopic(vote1.getVoteTitle(), "NonExistingTopic");

        assertFalse(result);
    }

    @Test
    public void voteInTopicSuccessfully() {
        Topic topic1 = Topic.builder()
                .topicName("Topic1")
                .votes(new HashMap<>())
                .build();
        Map<String, Integer> options = new HashMap<>();
        options.put("Yes", 0);
        options.put("No", 0);
        Vote vote1 = Vote.builder()
                .voteTitle("Vote1")
                .creator("Creator")
                .description("Desc")
                .options(options)
                .voters(new HashSet<>())
                .build();
        votingDao.save(topic1);
        votingDao.addVoteToTopic(vote1, topic1);

        boolean result = votingDao.voteInTopic(topic1.getTopicName(), vote1.getVoteTitle(), "Zikerok", "Yes");

        assertTrue(result);
        Integer actualCountOfVotes = votingDao.findByName(topic1.getTopicName())
                .get()
                .getVotes()
                .get(vote1.getVoteTitle())
                .getOptions()
                .get("Yes");
        assertThat(actualCountOfVotes).isEqualTo(1);
    }

    @Test
    public void isVoteExistsInTopic() {
        Topic topic1 = Topic.builder()
                .topicName("Topic1")
                .votes(new HashMap<>())
                .build();
        Vote vote1 = Vote.builder()
                .voteTitle("Vote1")
                .creator("Creator")
                .description("Desc")
                .options(Map.of("Yes", 0, "No", 0))
                .voters(new HashSet<>())
                .build();
        votingDao.save(topic1);
        votingDao.addVoteToTopic(vote1, topic1);

        boolean exists = votingDao.isVoteExistsInTopic(topic1.getTopicName(), vote1.getVoteTitle());

        assertTrue(exists, "Голосование должно существовать в топике");
    }
}