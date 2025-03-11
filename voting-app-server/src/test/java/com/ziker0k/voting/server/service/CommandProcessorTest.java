package com.ziker0k.voting.server.service;

import com.ziker0k.voting.common.service.VotingService;
import com.ziker0k.voting.server.command.LoginCommand;
import com.ziker0k.voting.server.model.UserSession;
import com.ziker0k.voting.server.model.UserState;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CommandProcessorTest {

    private CommandProcessor commandProcessor;
    private UserSession userSession;
    private VotingService votingService;
    private CommandRegistry commandRegistry;
    private ChannelHandlerContext channelHandlerContext;

    @BeforeEach
    void setUp() {
        commandRegistry = mock(CommandRegistry.class);
        commandProcessor = new CommandProcessor(commandRegistry);
        userSession = mock(UserSession.class);
        votingService = mock(VotingService.class);
        channelHandlerContext = mock(ChannelHandlerContext.class);
    }

    @Test
    void shouldSendErrorMessageWhenCommandIsEmpty() {
        String message = "";
        doReturn(channelHandlerContext).when(userSession).getChannelHandlerContext();
        doReturn(null).when(channelHandlerContext).writeAndFlush(any());

        commandProcessor.process(userSession, votingService, message);

        verify(userSession).getChannelHandlerContext();
        verify(userSession.getChannelHandlerContext()).writeAndFlush("Error! Empty command.\n");
    }

    @Test
    void shouldProcessValidCommand() {
        String message = "login -u=user1";
        LoginCommand mockCommand = mock(LoginCommand.class);
        doReturn(UserState.CONNECTED).when(userSession).getState();
        doReturn(mockCommand).when(commandRegistry).getCommand(any());
        doNothing().when(mockCommand).execute(any(UserSession.class), any(VotingService.class), anyString());

        commandProcessor.process(userSession, votingService, message);

        verify(mockCommand).execute(eq(userSession), eq(votingService), eq("-u=user1"));
    }

    @Test
    void shouldSendErrorMessageWhenCommandIsUnknown() {
        String message = "unknownCommand";
        doReturn(UserState.LOGGED_IN).when(userSession).getState();
        doReturn(null).when(commandRegistry).getCommand(any());
        doReturn(channelHandlerContext).when(userSession).getChannelHandlerContext();
        doReturn(null).when(channelHandlerContext).writeAndFlush(any());

        commandProcessor.process(userSession, votingService, message);

        verify(userSession).getChannelHandlerContext();
        verify(userSession.getChannelHandlerContext()).writeAndFlush("Unknown command.\n");
    }

    @Test
    void shouldSendErrorMessageWhenLoginIsRequired() {
        when(userSession.getState()).thenReturn(UserState.CONNECTED);
        String message = "create topic -t=TestTopic";
        doReturn(UserState.CONNECTED).when(userSession).getState();
        doReturn(channelHandlerContext).when(userSession).getChannelHandlerContext();
        doReturn(null).when(channelHandlerContext).writeAndFlush(any());

        commandProcessor.process(userSession, votingService, message);

        verify(userSession).getChannelHandlerContext();
        verify(userSession.getChannelHandlerContext()).writeAndFlush("Please login (login -u=username)\n");
    }

    @Test
    void shouldCombineTwoCommandsWithSpace() {
        String[] words = "create vote -t=TestTopic".split("\\s+");
        doReturn(true).when(commandRegistry).hasCommand("create vote");

        String actual = commandProcessor.combine(words);

        assertEquals("create vote", actual);
    }
}