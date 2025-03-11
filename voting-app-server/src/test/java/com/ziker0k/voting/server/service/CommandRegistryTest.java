package com.ziker0k.voting.server.service;

import com.ziker0k.voting.server.command.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CommandRegistryTest {
    private CommandRegistry commandRegistry;

    @BeforeEach
    void setUp() {
        commandRegistry = new CommandRegistry();
    }

    @Test
    void shouldContainAllRegisteredCommands() {
        assertTrue(commandRegistry.hasCommand("login"));
        assertTrue(commandRegistry.hasCommand("create topic"));
        assertTrue(commandRegistry.hasCommand("create vote"));
        assertTrue(commandRegistry.hasCommand("view"));
        assertTrue(commandRegistry.hasCommand("vote"));
        assertTrue(commandRegistry.hasCommand("delete"));
    }

    @Test
    void shouldReturnCorrectCommandByName() {
        Command loginCommand = commandRegistry.getCommand("login");
        assertNotNull(loginCommand);
        assertEquals("login", loginCommand.getName());

        Command viewCommand = commandRegistry.getCommand("view");
        assertNotNull(viewCommand);
        assertEquals("view", viewCommand.getName());
    }

    @Test
    void shouldReturnNullForUnknownCommand() {
        assertNull(commandRegistry.getCommand("unknownCommand"));
    }

    @Test
    void shouldReturnAllCommandNames() {
        Set<String> commandNames = commandRegistry.getCommandNames();
        assertEquals(6, commandNames.size());
        assertTrue(commandNames.contains("login"));
        assertTrue(commandNames.contains("create topic"));
        assertTrue(commandNames.contains("create vote"));
        assertTrue(commandNames.contains("view"));
        assertTrue(commandNames.contains("vote"));
        assertTrue(commandNames.contains("delete"));
    }
}