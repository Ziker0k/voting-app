package com.ziker0k.voting.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote implements Serializable {
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private final ReentrantLock lock = new ReentrantLock();
    private String voteTitle;
    private String creator;
    private String description;
    private Map<String, Integer> options;
    private Set<String> voters;

    // Голосование за вариант
    public boolean vote(String username, String option) {
        if (voters.contains(username)) {
            return false; // Пользователь уже проголосовал
        }

        // Проверяем, существует ли такой вариант ответа
        if (!options.containsKey(option)) {
            return false; // Некорректный вариант
        }

        options.computeIfPresent(option, (_, v) -> v + 1);// Записываем голос
        voters.add(username);
        return true; // Успешно проголосовали
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}