package com.ziker0k.voting.server.service;

import com.ziker0k.voting.server.command.LoadServerCommand;
import com.ziker0k.voting.server.command.SaveServerCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.Mockito.*;

//TODO Write better tests or rewrite ServerConsole.class
class ServerConsoleTest {
    private ServerConsole serverConsole;
    private SaveServerCommand saveCommand;
    private LoadServerCommand loadCommand;

    @BeforeEach
    void setUp() {
        saveCommand = mock(SaveServerCommand.class);
        loadCommand = mock(LoadServerCommand.class);
        serverConsole = new ServerConsole(saveCommand, loadCommand);
    }

    @Test
    void processCommand_SaveCommand_ExecutesSuccessfully() {
        serverConsole.processCommand("save data");

        verify(saveCommand).execute(any());
    }

    @Test
    void processCommand_LoadCommand_ExecutesSuccessfully() {
        serverConsole.processCommand("load backup");

        verify(loadCommand).execute(any());
    }

    @Test
    void processCommand_UnknownCommand_ShowsWarning() {
        serverConsole.processCommand("unknownCommand");
        verify(saveCommand, never()).execute(any());
        verify(loadCommand, never()).execute(any());
    }

    @Test
    void testProcessCommand_EmptyInput() {
        serverConsole.processCommand("");
        verify(saveCommand, never()).execute(any());
        verify(loadCommand, never()).execute(any());
    }

    @Test
    void testProcessCommand_CommandThrowsException() {
        doThrow(new RuntimeException("Mock exception")).when(saveCommand).execute(any());

        serverConsole.processCommand("save data.json");

        verify(saveCommand).execute(any());
    }

    @Test
    void testStartListening_ExitCommandTerminatesLoop() {
        String simulatedInput = "exit\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        serverConsole.startListening();

        System.setIn(originalIn);
    }
}