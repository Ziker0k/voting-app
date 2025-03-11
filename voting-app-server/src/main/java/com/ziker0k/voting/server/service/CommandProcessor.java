package com.ziker0k.voting.server.service;

import com.ziker0k.voting.common.service.VotingService;
import com.ziker0k.voting.server.command.Command;
import com.ziker0k.voting.server.model.UserSession;
import com.ziker0k.voting.server.model.UserState;

import java.util.Set;

public class CommandProcessor {
    private final CommandRegistry commandRegistry;
    private static final Set<String> PUBLIC_COMMANDS = Set.of("login");


    public CommandProcessor(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    public CommandProcessor() {
        this.commandRegistry = new CommandRegistry();
    }

    public void process(UserSession userSession, VotingService votingService, String message) {

        String[] words = message.trim().split("\\s+");

        // Проверка на пустую команду
        if (words.length < 2 && words[0].isBlank()) {
            sendErrorMessage(userSession, "Error! Empty command.");
            return;
        }

        // Объединение двух команд в одну, если это необходимо
        String commandName = combine(words);

        String args = message.substring(commandName.length()).trim();

        // Проверка на необходимость логина
        if (isAccessDenied(userSession, commandName)) {
            sendErrorMessage(userSession, "Please login (login -u=username)");
            return;
        }

        // Получение и выполнение команды
        Command command = commandRegistry.getCommand(commandName);
        if (command != null) {
            command.execute(userSession, votingService, args);
        } else {
            sendErrorMessage(userSession, "Unknown command.");
        }
    }

    public String combine(String[] words) {
        String commandName = words[0];
        if (words.length > 1 && commandRegistry.hasCommand(commandName + " " + words[1])) {
            commandName += " " + words[1];
        }
        return commandName;
    }

    private boolean isAccessAllowed(UserSession userSession, String commandName) {
        if (userSession.getState() == UserState.LOGGED_IN) {
            return true;
        }
        return PUBLIC_COMMANDS.contains(commandName);
    }

    private boolean isAccessDenied(UserSession userSession, String commandName) {
        return !isAccessAllowed(userSession, commandName);
    }

    private void sendErrorMessage(UserSession userSession, String message) {
        // Упрощаем отправку сообщений об ошибках
        userSession.getChannelHandlerContext().writeAndFlush(message + "\n");
    }
}