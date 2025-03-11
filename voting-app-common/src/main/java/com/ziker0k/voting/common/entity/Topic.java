package com.ziker0k.voting.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Topic implements Serializable {
    private String topicName;
    private Map<String, Vote> votes;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private final ReentrantLock lock = new ReentrantLock(); // Блокировка на уровне топика

    // Добавить голосование в топик
    public boolean addVote(Vote vote) {
        lock.lock(); // Блокировка на уровне топика
        try {
            if (votes.containsKey(vote.getVoteTitle())) {
                return false; // Голосование с таким названием уже существует
            }
            votes.put(vote.getVoteTitle(), vote); // Добавляем голосование в топик
            return true;
        } finally {
            lock.unlock(); // Обязательно разблокировать
        }
    }

    // Удалить голосование
    public boolean removeVote(String voteTitle) {
        lock.lock(); // Блокировка на уровне топика
        try {
            if (votes.containsKey(voteTitle)) {
                votes.remove(voteTitle); // Удаляем голосование
                return true;
            }
            return false;
        } finally {
            lock.unlock(); // Обязательно разблокировать
        }
    }

    // Получить голосование по названию
    public Vote getVote(String voteTitle) {
        lock.lock(); // Блокировка на уровне топика
        try {
            return votes.get(voteTitle); // Возвращаем голосование
        } finally {
            lock.unlock(); // Обязательно разблокировать
        }
    }

    // Получить все голосования в топике
    public Map<String, Vote> getVotes() {
        return new HashMap<>(votes); // Возвращаем копию
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}