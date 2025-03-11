package com.ziker0k.voting.app;

import com.ziker0k.voting.server.VotingServer;

public class ServerLauncher implements Launcher {

    @Override
    public void start() {
        try {
            new VotingServer(8060).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
