package com.ziker0k.voting.common.dao;

import com.ziker0k.voting.common.entity.Topic;
import com.ziker0k.voting.common.entity.Vote;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VotingDao {

    private static final VotingDao INSTANCE = new VotingDao();

    private final Map<String, Topic> topics = new HashMap<>();
    private final ReentrantReadWriteLock topicsLock = new ReentrantReadWriteLock();

    public static VotingDao getInstance() {
        return INSTANCE;
    }

/*
        TODO Work with topics
 */

    public Optional<Topic> save(Topic topic) {
        topicsLock.writeLock().lock();
        try {
            if (topics.containsKey(topic.getTopicName())) {
                return Optional.empty();
            }
            topics.put(topic.getTopicName(), topic);
            return Optional.of(topics.get(topic.getTopicName()));
        } finally {
            topicsLock.writeLock().unlock();
        }
    }

    public List<Topic> findAll() {
        topicsLock.readLock().lock();
        try {
            return topics.values().stream().toList();
        } finally {
            topicsLock.readLock().unlock();
        }
    }

    public Optional<Topic> findByName(String topicTitle) {
        topicsLock.readLock().lock();
        try {
            return Optional.ofNullable(topics.get(topicTitle));
        } finally {
            topicsLock.readLock().unlock();
        }
    }

    public Topic update(Topic topic) {
        topicsLock.writeLock().lock();
        try {
            return topics.replace(topic.getTopicName(), topic);
        } finally {
            topicsLock.writeLock().unlock();
        }
    }

    public boolean delete(String name) {
        topicsLock.writeLock().lock();
        try {
            return topics.remove(name) != null;
        } finally {
            topicsLock.writeLock().unlock();
        }
    }

    public Map<String, Topic> getTopicsToFile() {
        topicsLock.readLock().lock();
        try {
            return new HashMap<>(topics);
        } finally {
            topicsLock.readLock().unlock();
        }
    }

    public void writeTopicsFromFile(Map<String, Topic> topics) {
        topicsLock.writeLock().lock();
        try {
            this.topics.clear();
            this.topics.putAll(topics);
        } finally {
            topicsLock.writeLock().unlock();
        }
    }

    /*
            TODO Work with votes
     */
    // Добавление голосования в топик
    public boolean addVoteToTopic(Vote vote, Topic input) {
        topicsLock.readLock().lock();
        try {
            Topic topic = topics.get(input.getTopicName());
            if (topic == null) {
                return false; // Топик не найден
            }

            // Блокировка на уровне топика для безопасной модификации голосований
            topic.lock(); // Блокируем топик на время модификации

            try {
                return topic.addVote(vote); // Добавляем голосование в топик
            } finally {
                topic.unlock(); // Освобождаем блокировку
            }
        } finally {
            topicsLock.readLock().unlock();
        }
    }

    // Удаление голосования из топика
    public boolean removeVoteFromTopic(String voteTitle, String topicTitle) {
        topicsLock.readLock().lock();
        try {
            Topic topic = topics.get(topicTitle);

            if (topic == null) {
                return false; // Топик не найден
            }

            // Блокировка на уровне топика для безопасной модификации голосований
            topic.lock(); // Блокируем топик на время удаления голосования

            try {
                return topic.removeVote(voteTitle); // Удаляем голосование из топика
            } finally {
                topic.unlock(); // Освобождаем блокировку
            }
        } finally {
            topicsLock.readLock().unlock();
        }
    }

    public boolean voteInTopic(String topicName, String voteTitle, String username, String option) {
        topicsLock.readLock().lock();
        try {
            Topic topic = topics.get(topicName);
            if (topic == null) {
                return false; // Топик не найден
            }

            Vote vote = topic.getVote(voteTitle);
            if (vote == null) {
                return false; // Голосование не найдено
            }

            vote.lock();
            try {
                return vote.vote(username, option);
            } finally {
                vote.unlock();
            }
        } finally {
            topicsLock.readLock().unlock();
        }
    }

    // Проверка на существование голосования в топике
    public boolean isVoteExistsInTopic(String topicName, String voteTitle) {
        topicsLock.readLock().lock();
        try {
            Topic topic = topics.get(topicName);
            return topic != null && topic.getVote(voteTitle) != null;
        } finally {
            topicsLock.readLock().unlock();
        }
    }

    //    TODO:DELETE
//     For test
    public void clear() {
        topics.clear();
    }
}