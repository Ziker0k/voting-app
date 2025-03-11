package com.ziker0k.voting.server.command;

import com.ziker0k.voting.common.service.VotingService;
import com.ziker0k.voting.server.model.UserSession;
import com.ziker0k.voting.server.model.UserState;
import com.ziker0k.voting.server.service.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginCommand implements Command {

    private static final Logger log = LoggerFactory.getLogger(LoginCommand.class);
    private static final String LOGIN_PREFIX = "-u=";
    private static final String ERROR_USERNAME_TAKEN = "Error! Username \"%s\" is already taken.\n";
    private static final String ERROR_INVALID_FORMAT = "Error! Use: login -u=username\n";
    private static final String ERROR_EMPTY_USERNAME = "Error! Username shouldn't be empty.\n";
    private static final String LOGIN_SUCCESS = "You logged in as \"%s\"\n";
    private static final String ALREADY_LOGGED_IN = "You already logged in as %s\n";
    private static final String AUTH_ERROR = "Authorization error!\n";
    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public String getName() {
        return "login";
    }

    @Override
    public void execute(UserSession userSession, VotingService votingService, String args) {
        ChannelHandlerContext ctx = userSession.getChannelHandlerContext();

        // Check if the user is already logged in
        if (userSession.getState() == UserState.LOGGED_IN && userSession.getUsername() != null) {
            log.info("User {} is already logged in.", userSession.getUsername());
            ctx.writeAndFlush(String.format(ALREADY_LOGGED_IN, userSession.getUsername()));
            return;
        }

        // Validate command argument format
        if (!args.startsWith(LOGIN_PREFIX)) {
            log.warn("Invalid login attempt. Expected format: login -u=username");
            ctx.writeAndFlush(ERROR_INVALID_FORMAT);
            return;
        }

        String username = args.replace(LOGIN_PREFIX, "").trim();

        // Validate the username
        if (username.isEmpty()) {
            log.warn("Attempted login with empty username.");
            ctx.writeAndFlush(ERROR_EMPTY_USERNAME);
            return;
        }

        // Check if the username is already taken
        if (sessionManager.isUserLoggedIn(username)) {
            log.warn("Attempt to login with already taken username: {}", username);
            ctx.writeAndFlush(String.format(ERROR_USERNAME_TAKEN, username));
            return;
        }

        // Register the user and change session state
        if (sessionManager.registerUser(username, userSession)) {
            userSession.setUsername(username);
            userSession.setState(UserState.LOGGED_IN);
            log.info("User {} logged in successfully.", username);
            ctx.writeAndFlush(String.format(LOGIN_SUCCESS, username));
        } else {
            log.error("Authorization error for username: {}", username);
            ctx.writeAndFlush(AUTH_ERROR);
        }
    }
}