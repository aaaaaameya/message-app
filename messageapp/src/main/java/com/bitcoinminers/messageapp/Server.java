package com.bitcoinminers.messageapp;

import java.util.ArrayList;
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
        for (Chat chat : chats) {
            if (chat.getId() == chatId) {
                return chat.getMessages();
            }
        }

        throw new NoSuchElementException(String.format("No chat with ID %d.", chatId));
    }

    public void newUser(String name) {
        users.add(new User(getNextId(), name));
    }

    public void newChat(String name) {
        chats.add(new Chat(getNextId(), name));
    }

    /**
     * 
     * @param chatId Potentially valid chat ID.
     * @param sender Name of the sender.
     * @param contents Contents of the message.
     * @throws NoSuchElementException If there is no chat with id
     * {@code chatId}.
     */
    public void postMessage(int chatId, String sender, String contents) throws NoSuchElementException {
        for (Chat chat : chats) {
            if (chat.getId() == chatId) {
                chat.addMessage(sender, contents);
                return;
            }
        }

        throw new NoSuchElementException(String.format("No chat with id %d", chatId));
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