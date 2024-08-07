package com.bitcoinminers.messageapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

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
        if (existingUsers.contains(userId)) {
            System.err.println("user already in chat");
            return;
        }
        user.addChat(chatId);
        chat.addUser(userId);
        user.joinGroupChat(chat);;

        if (existingUsers.size() == 1) {
            chat.makeAdmin(userId);
        } 

    }

    public void removeUserFromChat(int removerId, int userId, int chatId) {
        User remover = getUser(removerId);
        getUser(userId);
        Chat chat = getChat(chatId);
        if (chat.getUsers().contains(removerId) && chat.getUsers().contains(userId)) {
            chat.removeUser(userId);
            System.err.printf("user %d removed from chat %d, new keys will now be computed\n", userId, chatId);
            remover.generateGroupSecret(chat);
        } else {
            System.err.printf("user was not removed");
        }
        
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
    public ArrayList<Message> getMessages(int chatId) throws NoSuchElementException {
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
        users.add(new User(getNextId(), name, this));
    }

    public void newChat(String name) {
        chats.add(new Chat(getNextId(), name));
    }

    public void ping(Integer chatId, HashMap<Integer, byte[]> encryptedSecrets) throws Exception{
        try {
            for (int u : getChat(chatId).getUsers()) {
                getUser(u).receivePing(getChat(chatId), encryptedSecrets.get(u));
            }

        } catch (Exception e) {
            System.err.println(e);
        }
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
    public int storeEncryptedMessage(int chatId, Message m) throws NoSuchElementException {
        Chat chat = getChat(chatId);
        return chat.addMessage(m);
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
