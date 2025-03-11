package com.ziker0k.voting.app;

import com.ziker0k.voting.client.VotingClient;

public class ClientLauncher implements Launcher {

    @Override
    public void start() {
        try {
            new VotingClient().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
