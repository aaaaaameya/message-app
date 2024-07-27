package com.bitcoinminers.messageapp;

import java.util.ArrayList;

import org.json.JSONObject;

/**
 * Group messaging chat.
 * @author Christian Albina
 */
public class Chat implements Saveable {

    /**
     * 
     */
    private ArrayList<User> users = new ArrayList<>();

    /**
     * Unique ID of this chat.
     */
    private int id;

    /**
     * Name of the chat.
     */
    private String name;

    /**
     * Messages in the chat.
     */
    private ArrayList<Message> messages = new ArrayList<>();

    public Chat(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
        }
    }

    public void addMessage(String sender, String contents) {
        messages.add(new Message(sender, contents));
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
