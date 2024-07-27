package com.bitcoinminers.messageapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

import com.bitcoinminers.messageapp.exceptions.UnimplementedCommandException;
import com.bitcoinminers.messageapp.exceptions.UnknownCommandException;

/**
 * Class to simulate a sudo session.
 * @author Christian Albina
 */
public class Session {
    /**
     * State of the session.
     */
    boolean active = true;

    /**
     * ID of the current user view.
     */
    private int currUserId = -1;

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
        // return currUserId.get();
        return currUserId;
    }

    private String getCurrUserName() throws NoSuchElementException {
        for (User user : server.getUsers()) {
            if (user.getId() == currUserId) {
                return user.getName();
            }
        }

        throw new NoSuchElementException();
    }

    public void handleCommand(String command) {
        String[] commands = command.split("\\s+");

        try {
            switch (commands[0]) {
                case "h":  case "help":     helpCommand(); break;
                case "q":  case "quit":     quitCommand(); break;
                case "u":  case "users":    usersCommand(); break;
                case "c":  case "chats":    chatsCommand(); break;
                case "nu": case "new-user": newUserCommand(String.join(" ", Arrays.copyOfRange(commands, 1, commands.length))); break;
                case "v":  case "view":     switchViewCommand(Integer.parseInt(commands[1])); break;
                case "m":  case "message":  messageCommand(Integer.parseInt(commands[1]), String.join(" ", Arrays.copyOfRange(commands, 2, commands.length))); break;
                case "lu":  case "log-u" :     logUsersCommand(Integer.parseInt(commands[1])); break;
                case "lm":  case "log-m" :     logMessagesCommand(Integer.parseInt(commands[1])); break;
                case "nc": case "new-chat": newChatCommand(String.join(" ", Arrays.copyOfRange(commands, 1, commands.length))); break;
                case "d":  case "delete":   deleteCommand(Integer.parseInt(commands[1])); break;
                case "a":  case "add":      addCommand(Integer.parseInt(commands[1]), Integer.parseInt(commands[2])); break;
                case "r":  case "remove":   removeCommand(Integer.parseInt(commands[1]), Integer.parseInt(commands[2])); break;
                case "s":  case "save":     saveCommand(); break;
                default: throw new UnknownCommandException(commands[0]);
            }
        } catch (Exception exception) {
            System.out.printf("An error occured: \"%s\"\n", exception.getMessage());
        }
    }

    public void helpCommand() {
        System.out.println("misc:");
        System.out.println("  h(help):          Show all commands.");
        System.out.println("  q(quit):          Quit session.");
        System.out.println();
        System.out.println("sudo commands:");
        System.out.println("  u(user):          Show all user IDs and names.");
        System.out.println("  c(chats):         Show all chat IDs and names.");
        System.out.println("  nu(new-user) N:   Create new user device with name N.");
        System.out.println("  v(view) X:        Switch to user device with ID X.");
        System.out.println("  s(save):          Save the current system state.");
        System.out.println();
        System.out.println("user commands");
        System.out.println("  m(message) X M:   Try to send message M to chat with id X.");
        System.out.println("  lu(log-u) X:      Request the chat log of the group chat with ID X.");
        System.out.println("  lm(log-m) X:      Request the chat log of the group chat with ID X.");
        System.out.println("  nc(new-chat) N:   Create a new chat with name N.");
        System.out.println("  d(delete) X:      Try to delete chat with ID X.");
        System.out.println("  a(add) X1 X2:     Try to add user with ID X1 to chat with ID X2.");
        System.out.println("  r(remove) X1 X2:  Try to remove user with ID X1 from chat with ID X2.");
    }

    public void quitCommand() {
        active = false;
    }

    public void usersCommand() {
        server.getUsers().forEach(user -> System.out.printf("%d: %s\n", user.getId(), user.getName()));
    }

    public void chatsCommand() {
        server.getChats().forEach(chat -> System.out.printf("%d: %s\n", chat.getId(), chat.getName()));
    }

    public void newUserCommand(String name) {
        server.newUser(name);
    }

    public void newChatCommand(String name) {
        server.newChat(name);
    }

    public void switchViewCommand(int userId) {
        // currUserId = Optional.of(userId);
        if (server.hasUser(userId) || userId == -1) currUserId = userId;
    }

    public void messageCommand(int chatId, String contents) {
        server.postMessage(chatId, getCurrUserName(), contents);
    }

    public void logUsersCommand(int chatId) {
        ArrayList<Integer> userIds = server.getChatUsers(chatId);
        System.out.printf("Users in chat %d: %s\n", chatId, userIds.toString());
    }

    public void logMessagesCommand(int chatId) {
        ArrayList<Message> messages = server.getMessages(chatId, currUserId);
        for (Message m : messages) {
            System.out.print(m.toString());
        }
    }

    private void deleteCommand(int chatId) {
        throw new UnimplementedCommandException("delete command");
    }
    
    private void addCommand(int userId, int chatId) {
        server.addUserToChat(userId, chatId);
    }
    
    private void removeCommand(int userId, int chatId) {
        throw new UnimplementedCommandException("remove command");
    }
    
    private void saveCommand() {
        throw new UnimplementedCommandException("save command");
    }

    public static void main(String[] args) {
        System.out.println("Starting session...");

        Session session = new Session(new Server());
        Scanner sc = new Scanner(System.in);

        System.out.println("\nType \"help\" to see all commands.");

        while (session.isActive()) {
            System.out.print(CommandLineColours.ANSI_GREEN);

            // try {
            //     System.out.print(session.getCurrUserId());
            // } catch (NoSuchElementException e) {
            //     System.out.print("?");
            // }

            if (session.getCurrUserId() == -1) {
                System.out.print("SUDO");
            } else {
                System.out.print(session.getCurrUserId());
            }

            System.out.print(CommandLineColours.ANSI_RESET + ": " + CommandLineColours.ANSI_BLUE);
            final String command = sc.nextLine();
            System.out.print(CommandLineColours.ANSI_RESET);

            session.handleCommand(command);
        }

        sc.close();
    }
}
