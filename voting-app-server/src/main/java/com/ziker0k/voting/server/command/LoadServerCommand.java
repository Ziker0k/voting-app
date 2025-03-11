package com.ziker0k.voting.server.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ziker0k.voting.common.service.VotingService;
import com.ziker0k.voting.server.model.Data;

import java.io.*;

public class LoadServerCommand implements ServerCommand {
    private final VotingService votingService = VotingService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getName() {
        return "load";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: load <filename>");
            return;
        }

        String filename = args[1] + ".json";

        try {
            Data data = objectMapper.readValue(new File(filename), Data.class);
            votingService.loadTopics(data.getTopics());
            System.out.println("Data successfully loaded from " + filename);
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }
}
