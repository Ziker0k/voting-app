package com.ziker0k.voting.server.command;

public interface ServerCommand {
    String getName();

    void execute(String[] args);
}
