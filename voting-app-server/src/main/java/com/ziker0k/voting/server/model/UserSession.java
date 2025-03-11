package com.ziker0k.voting.server.model;

import com.ziker0k.voting.common.dto.DoVoteDto;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UserSession {
    private final ChannelHandlerContext channelHandlerContext;
    private UserState state = UserState.CONNECTED;
    private DoVoteDto doVoteDto;
    private String username;
    private String currentTopic;
    private String currentVoteName;
    private String currentVoteDesc;
    private int optionCount;
    private List<String> options = new ArrayList<>();

    public UserSession(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public void setCurrentVote(String topic, String voteName, String voteDesc) {
        this.currentTopic = topic;
        this.currentVoteName = voteName;
        this.currentVoteDesc = voteDesc;
    }

    public void resetVoteData() {
        currentTopic = null;
        currentVoteName = null;
        currentVoteDesc = null;
        optionCount = 0;
        options.clear();
    }

    public void activeVote(List<String> options, DoVoteDto doVoteDto) {
        setState(UserState.VOTING);
        this.options.addAll(options);
        this.doVoteDto = doVoteDto;
    }

    public void clearActiveVote() {
        setState(UserState.LOGGED_IN);
        this.options.clear();
        this.doVoteDto = null;
    }
}