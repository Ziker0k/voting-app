package com.ziker0k.voting.server.service;

import com.ziker0k.voting.server.command.LoadServerCommand;
import com.ziker0k.voting.server.command.SaveServerCommand;
import com.ziker0k.voting.server.command.ServerCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ServerConsole {
    private static final Logger logger = LoggerFactory.getLogger(ServerConsole.class);
    private final Map<String, ServerCommand> commands = new HashMap<>();

    public ServerConsole() {
        this(new SaveServerCommand(), new LoadServerCommand());
    }

    public ServerConsole(SaveServerCommand saveCommand, LoadServerCommand loadCommand) {
        commands.put("save", saveCommand);
        commands.put("load", loadCommand);
    }

    // Метод для обработки команд с проверкой на существование команды и с учетом параметров
    public void processCommand(String input) {
        String[] parts = input.trim().split("\\s+"); // split по пробелам, но без пустых строк

        if (parts.length < 2 && parts[0].isBlank()) {
            logger.warn("No command entered.");
            System.out.println("No command entered.");
            return;
        }

        String commandName = parts[0];
        ServerCommand command = commands.get(commandName);

        if (command != null) {
            try {
                command.execute(parts);
                logger.info("Executed command: {}", commandName); // Логируем успешное выполнение команды
            } catch (Exception e) {
                logger.error("Error executing command '{}': {}", commandName, e.getMessage());
                System.out.println("Error executing command: " + e.getMessage());
            }
        } else {
            logger.warn("Unknown command: {}", commandName);
            System.out.println("Unknown command: " + commandName);
        }
    }

    // Метод для начала прослушивания консоли с улучшенной обработкой ошибок и добавленной командой help
    public void startListening() {
        try (Scanner scanner = new Scanner(System.in)) {
            String input;
            while (true) {
                System.out.print("> ");
                input = scanner.nextLine().trim(); // Убираем лишние пробелы
                if ("exit".equalsIgnoreCase(input)) {
                    logger.info("Server shutdown initiated by user.");
                    System.out.println("Shutting down server...");
                    break; // Прерываем цикл для завершения работы
                }
                processCommand(input);
            }
        } catch (Exception e) {
            logger.error("Error with console input: {}", e.getMessage());
            System.out.println("Error with console input: " + e.getMessage());
        }
    }
}