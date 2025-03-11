package com.ziker0k.voting.app;

public class VotingApplicationRunner {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Использование: java -jar app.jar <server|client>");
            return;
        }

        Launcher launcher = getLauncher(args[0]);
        if (launcher != null) {
            launcher.start();
        } else {
            System.out.println("Неизвестный режим. Используйте 'server' или 'client'.");
        }
    }

    private static Launcher getLauncher(String mode) {
        return switch (mode.toLowerCase()) {
            case "server" -> new ServerLauncher();
            case "client" -> new ClientLauncher();
            default -> null;
        };
    }
}
