package com.ziker0k.voting.common.service;

import com.ziker0k.voting.common.dao.VotingDao;
import com.ziker0k.voting.common.dto.CreateVoteDto;
import com.ziker0k.voting.common.dto.DoVoteDto;
import com.ziker0k.voting.common.dto.TopicDto;
import com.ziker0k.voting.common.entity.Topic;
import com.ziker0k.voting.common.entity.Vote;
import com.ziker0k.voting.common.exception.VotingException;
import com.ziker0k.voting.common.mapper.CreateVoteMapper;
import com.ziker0k.voting.common.mapper.TopicMapper;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class VotingService {
    private static final VotingService INSTANCE = new VotingService();
    private final VotingDao votingDao = VotingDao.getInstance();
    private final CreateVoteMapper createVoteMapper = CreateVoteMapper.getInstance();
    private final TopicMapper topicMapper = TopicMapper.getInstance();

    public static VotingService getInstance() {
        return INSTANCE;
    }

    public boolean createTopic(String name) {
        Topic topic = Topic.builder()
                .topicName(name)
                .votes(new HashMap<>())
                .build();
        return votingDao.save(topic).isPresent();
    }

    public void createVote(CreateVoteDto createVoteDto) {

        Optional<Topic> maybeTopic = votingDao.findByName(createVoteDto.getTopicTitle());
        if (maybeTopic.isEmpty()) {
            throw new VotingException("Topic with title " + createVoteDto.getTopicTitle() + " already exists");
        }
        Topic topic = maybeTopic.get();
        if (topic.getVotes().containsKey(createVoteDto.getVoteTitle())) {
            throw new VotingException("Vote \"" + createVoteDto.getVoteTitle() + "\" already exists.");
        }

        Vote vote = createVoteMapper.map(createVoteDto);

        votingDao.addVoteToTopic(vote, topic);
    }


    public void vote(DoVoteDto doVoteDto) {

        Optional<Topic> topic = votingDao.findByName(doVoteDto.getTopicName());
        if (topic.isEmpty()) {
            throw new VotingException("Topic does not exist");
        }
        Vote vote = topic.get().getVotes().get(doVoteDto.getVoteName());
        if (vote == null) {
            throw new VotingException("Vote \"" + doVoteDto.getVoteName() + "\" does not exist");
        }
        if (!vote.getOptions().containsKey(doVoteDto.getOption())) {
            throw new VotingException("Option \"" + doVoteDto.getOption() + "\" does not exist");
        }

        votingDao.voteInTopic(topic.get().getTopicName(),
                doVoteDto.getVoteName(),
                doVoteDto.getVoter(),
                doVoteDto.getOption());
    }

    public boolean isTopicNotExists(String topicName) {
        return votingDao.findByName(topicName).isEmpty();
    }

    public boolean isVoteExistsInTopic(String trim, String topicTitle) {
        return votingDao.isVoteExistsInTopic(trim, topicTitle);
    }

    public List<TopicDto> getAllTopics() {
        return votingDao.findAll().stream().map(topicMapper::map).toList();
    }

    public TopicDto getTopic(String topicName) {
        return votingDao.findByName(topicName).map(topicMapper::map).orElse(null);
    }

    public void removeVote(String voteTitle, String topicTitle) {
        votingDao.removeVoteFromTopic(voteTitle, topicTitle);
    }

    public Map<String, Topic> getTopics() {
        return votingDao.getTopicsToFile();
    }

    public void loadTopics(Map<String, Topic> topics) {
        votingDao.writeTopicsFromFile(topics);
    }
}
