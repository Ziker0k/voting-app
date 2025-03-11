package com.ziker0k.voting.server.service;

import com.ziker0k.voting.server.command.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();

    public CommandRegistry() {
        register(new LoginCommand());
        register(new CreateTopicCommand());
        register(new CreateVoteCommand());
        register(new ViewCommand());
        register(new VoteCommand());
        register(new DeleteCommand());
    }

    private void register(Command command) {
        commands.put(command.getName(), command);
    }

    public boolean hasCommand(String name) {
        return commands.containsKey(name);
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }

    public Set<String> getCommandNames() {
        return commands.keySet();
    }
}
