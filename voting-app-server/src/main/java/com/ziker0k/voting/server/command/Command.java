package com.ziker0k.voting.server.command;

import com.ziker0k.voting.common.service.VotingService;
import com.ziker0k.voting.server.model.UserSession;

public interface Command {
    String getName();
    void execute(UserSession userSession, VotingService votingService, String args);
}
