package com.bitcoinminers.messageapp;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

/**
 * Class to simulate a user session. This is the interface with the server.
 * @author Christian Albina
 */
public class Session {
    /**
     * State of the session.
     */
    boolean active = true;

    /**
     * God view status
     */
    private boolean godView = true;
    private String godPassword = "bitcoin";

    /**
     * ID of the current user view.
     */
    private Optional<Integer> currUserId = Optional.empty();

    /**
     * Server this session is happening in.
     */
    final private Server server;

    public Session(Server server) {
        this.server = server;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * @return User ID currently being used.
     * @throws NoSuchElementException If no user is currently being used.
     */
    private int getCurrUserId() throws NoSuchElementException {
        return currUserId.get();
    }

    private String getCurrUserName() throws NoSuchElementException {
        if (this.godView) {
            return "god";
        }

        for (User user : server.getUsers()) {
            if (user.getId() == getCurrUserId()) {
                return user.getName();
            }
        }

        throw new NoSuchElementException();
    }

    public void handleCommand(String command) {
        String[] commands = command.split("\\s+");

        try {
            switch (commands[0]) {
                case "h":   case "help":     helpCommand(); break;
                case "q":   case "quit":     quitCommand(); break;
                case "u":   case "users":    usersCommand(); break;
                case "c":   case "chats":    chatsCommand(); break;
                case "nu":  case "new-user": newUserCommand(commands[1]); break;
                case "nc":  case "new-chat": newChatCommand(commands[1]); break;
                case "v":   case "view":     switchViewCommand(Integer.parseInt(commands[1])); break;
                case "m":   case "message":  messageCommand(Integer.parseInt(commands[1]), String.join(" ", Arrays.copyOfRange(commands, 2, commands.length))); break;
                case "l":   case "log" :     logCommand(Integer.parseInt(commands[1])); break;
                case "ia":  case "is-admin": isAdminCommand(Integer.parseInt(commands[1])); break;
                case "ta":  case "toggle-admin": toggleAdminCommand(Integer.parseInt(commands[1])); break;
                case "au":  case "add-user": addUserToChatCommand(Integer.parseInt(commands[1]), Integer.parseInt(commands[2])); break;
                case "ru":  case "remove-user": removeUserFromChatCommand(Integer.parseInt(commands[1]), Integer.parseInt(commands[2])); break;
                // TODO Implement a save command
            }
        } catch (Exception e) {
            System.out.printf("An error occured: \"%s\"\n", e.getMessage());
        }
    }

    public void helpCommand() {
        System.out.println("| h  / help:            show commands");
        System.out.println("| q  / quit:           quit the current session (will not save)");
        System.out.println("| u  / users:          show all user ids and names");
        System.out.println("| c  / chats:          show all chat ids and names");
        System.out.println("| nu / new-user N:     create a new user with name N");
        System.out.println("| nc / new-chat N:     create a new chat with name N");
        System.out.println("| v  / view X:         switch to user view of id X");
        System.out.println("| m  / message X M:    send message M to chat with id X");
        System.out.println("| l  / log X:          view chat log of chat with ID X");
        System.out.println("| ia / is-admin X:     get admin status for user of id X");
        System.out.println("| ta / toggle-admin X: toggle admin status for user of id X");
        System.out.println("| au / add-user X C:   add user of id X to chat with id C");
        System.out.println("| ru / remove-user X C: remove user of id X from chat with id C");
        System.out.println("| bg / be-god P:       enter god mode, requires password P");
    }

    public void quitCommand() {
        active = false;
    }

    public void usersCommand() {
        server.getUsers().forEach(user -> System.out.printf("| %d: %s\n", user.getId(), user.getName()));
    }

    public void chatsCommand() {
        server.getChats().forEach(chat -> System.out.printf("| %d: %s\n", chat.getId(), chat.getName()));
    }

    public void newUserCommand(String name) {
        server.newUser(name);
    }

    public void newChatCommand(String name) {
        server.newChat(name);
    }

    public void switchViewCommand(int userId) {
        // disable god view here?

        // Need error checking here?
        currUserId = Optional.of(userId);
    }

    public void messageCommand(int chatId, String contents) {
        server.postMessage(chatId, getCurrUserName(), contents);
    }

    public void logCommand(int chatId) {
        // TODO Route feedback of server message log fetch to user
        // for processing (will later include message decryption)
    }

    public void isAdminCommand(int userId) {
        if (server.checkAdmin(userId)) {
            System.out.printf("| User %d is admin\n", userId);
        } else {
            System.out.printf("| User %d is not admin\n", userId);
        }
    }

    public void toggleAdminCommand(int userId) {
        if (this.godView || server.checkAdmin(currUserId.get())) {
            server.toggleAdmin(userId);
        } else {
            System.out.println("You are not admin");
        }
    }

    public void addUserToChatCommand(int userId, int chatId) {
        if (this.godView || server.checkAdmin(currUserId.get())) {
            server.addUserToChat(userId, chatId);
        } else {
            System.out.println("You are not admin");
        }
    }

    public void removeUserFromChatCommand(int userId, int chatId) {
        if (this.godView || server.checkAdmin(currUserId.get())) {
            server.removeUserFromChat(userId, chatId);
        } else {
            System.out.println("You are not admin");
        }
    }

    public void enterGodModeCommand(String password) {
        if (password.equals(godPassword)) {
            this.godView = true;
        } else {
            System.out.println("You are not god");
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting session...");

        Session session = new Session(new Server());
        Scanner sc = new Scanner(System.in);

        System.out.println("Type \"help\" to see all commands.");

        while (session.isActive()) {
            try {
                System.out.print(session.getCurrUserId());
            } catch (NoSuchElementException e) {
                System.out.print("?");
            }

            System.out.print(": ");

            session.handleCommand(sc.nextLine());
        }

        sc.close();
    }
}
