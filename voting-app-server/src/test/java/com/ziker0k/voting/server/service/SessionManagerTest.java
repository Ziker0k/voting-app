package com.ziker0k.voting.server.service;

import com.ziker0k.voting.server.model.UserSession;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {
    private final SessionManager sessionManager = SessionManager.getInstance();
    private ChannelHandlerContext context;

    @BeforeEach
    void setUp() {
        context = Mockito.mock(ChannelHandlerContext.class);
    }

    @AfterEach
    void clear() {
        sessionManager.clear();
    }

    @Test
    void registerUser_Success() {
        String username = "testUser";
        UserSession userSession = sessionManager.getSession(context);

        boolean result = sessionManager.registerUser(username, sessionManager.getSession(userSession.getChannelHandlerContext()));

        assertTrue(result);
    }

    @Test
    void registerUser_Failure_UserAlreadyExists() {
        String username = "testUser";
        sessionManager.registerUser(username, sessionManager.getSession(context));

        ChannelHandlerContext anotherContext = Mockito.mock(ChannelHandlerContext.class);
        UserSession anotherSession = sessionManager.getSession(anotherContext);

        boolean result = sessionManager.registerUser(username, anotherSession);

        assertFalse(result);
    }

    @Test
    void isUserLoggedIn_UserExists() {
        String username = "testUser";

        sessionManager.registerUser(username, sessionManager.getSession(context));

        assertTrue(sessionManager.isUserLoggedIn(username));
    }

    @Test
    void isUserLoggedIn_UserNotExists() {
        assertFalse(sessionManager.isUserLoggedIn("unknownUser"));
    }

    @Test
    void getSession_NewSessionCreated() {
        UserSession actual = sessionManager.getSession(context);

        assertNotNull(actual);
        assertThat(actual).isSameAs(sessionManager.getSession(context));
    }

    @Test
    void destroySession_UserRemoved() {
        String username = "testUser";
        UserSession userSession = sessionManager.getSession(context);
        userSession.setUsername(username);
        sessionManager.registerUser(username, userSession);

        assertTrue(sessionManager.isUserLoggedIn(username));
        sessionManager.destroySession(userSession);

        assertFalse(sessionManager.isUserLoggedIn(username));
        assertNull(sessionManager.getSessions().get(context));
        assertThat(sessionManager.getSessions()).isEmpty();
    }

    @Test
    void destroySession_UserNotLoggedIn() {
        UserSession userSession = sessionManager.getSession(context);
        assertTrue(sessionManager.getSessions().containsKey(context));

        sessionManager.destroySession(userSession);

        assertFalse(sessionManager.getSessions().containsKey(context));
    }
}