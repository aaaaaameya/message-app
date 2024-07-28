package com.bitcoinminers.messageapp;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.json.JSONObject;

public class Server implements Saveable {
    /**
     * List of all group chats.
     */
    private ArrayList<Chat> chats = new ArrayList<>();

    /**
     * List of all user accounts.
     */
    private ArrayList<User> users = new ArrayList<>();

    /**
     * Next unused unique ID.
     */
    private static int nextId = 0;

    public ArrayList<Chat> getChats() {
        return chats;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public User getUser(int userId) throws NoSuchElementException {
        for (User user : users) {
            if (user.getId() == userId) return user;
        }

        throw new NoSuchElementException(String.format("No user with ID %d.", userId));
    }

    public Chat getChat(int chatId) throws NoSuchElementException {
        for (Chat chat : chats) {
            if (chat.getId() == chatId) return chat;
        }

        throw new NoSuchElementException(String.format("No chat with ID %d.", chatId));
    }

    public void addUserToChat(int userId, int chatId) throws NoSuchElementException {
        User user = getUser(userId);
        Chat chat = getChat(chatId);
        ArrayList<Integer> existingUsers = chat.getUsers();
        if (existingUsers.size() == 0) {
            user.initialiseChat(chatId);
        } else {
            User existing = getUser(existingUsers.get(0));
            // TODO: Implement Basic DH, then Basic Station-to-Station for anti MITM unless we wanna keep that as a vulnerability
            SecretKey s = existing.shareKey(chatId);
            user.addChat(chatId, s);
        }
        chat.addUser(userId);
    }

    public void removeUserFromChat(int userId, int chatId) {
        getUser(userId);
        Chat chat = getChat(chatId);
        chat.removeUser(userId);

    }

    public boolean hasUser(int userId) {
        for (User user : users) {
            if (user.getId() == userId) return true;
        }
        return false;
    }

    /**
     * Get the next unused unique ID and increment the underlying ID.
     * @return Unused unique ID.
     */
    public int getNextId() {
        return nextId++;
    }
    

    /**
     * Get the encrypted messages of a particular chat.
     * @param chatId ID of the chat.
     * @return Encrypted messages in the chat with ID {@code chatId}.
     * @throws NoSuchElementException If there is no chat with  ID
     * {@code chatId}.
     */
    public ArrayList<Message> getMessages(int chatId, int currUserId) throws NoSuchElementException {
        Chat chat = getChat(chatId);
        return chat.getMessages();
    }

    /**
     * Get the userIds of a particular chat.
     * @param chatId ID of the chat.
     * @return ID of users in the chat with ID {@code chatId}.
     * @throws NoSuchElementException If there is no chat with ID
     * {@code chatId}.
     */
    public ArrayList<Integer> getChatUsers(int chatId) throws NoSuchElementException {
        Chat chat = getChat(chatId);
        return chat.getUsers();
    }

    public void newUser(String name) {
        users.add(new User(getNextId(), name));
    }

    public void newChat(String name) {
        chats.add(new Chat(getNextId(), name));
    }

    /**
     * ASSUMPTION:
     * 
     * @param chatId Potentially valid chat ID.
     * @param sender Name of the sender.
     * @param contents Contents of the message.
     * @throws NoSuchElementException If there is no chat with id
     * {@code chatId}.
     */
    public void storeEncryptedMessage(int chatId, String sender, String encryptedContents, IvParameterSpec iv) throws NoSuchElementException {
        Chat chat = getChat(chatId);
        chat.addMessage(sender, encryptedContents, iv);
    }

    @Override
    public JSONObject toJson() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void fromJson(JSONObject obj) {
        // TODO Auto-generated method stub
        
    }
}
