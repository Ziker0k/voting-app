package com.ziker0k.voting.server.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ziker0k.voting.common.service.VotingService;
import com.ziker0k.voting.server.model.Data;

import java.io.File;
import java.io.IOException;

public class SaveServerCommand implements ServerCommand {
    private final VotingService votingService = VotingService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: save <filename>");
            return;
        }

        String filename = args[1] + ".json";

        try {
            Data data = new Data(votingService.getTopics());
            objectMapper.writeValue(new File(filename), data);
            System.out.println("Data successfully saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }
}