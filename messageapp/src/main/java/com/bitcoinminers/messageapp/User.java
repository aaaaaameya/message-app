package com.bitcoinminers.messageapp;

import java.util.ArrayList;

import org.json.JSONObject;

/**
 * User of message-app. Simulates an individual device (such as a
 * personal phone).
 * @author Christian Albina
 */
public class User implements Saveable {
    /**
     * Unique ID of user.
     */
    private int id;

    /**
     * Name of user.
     */
    private String name;

    /**
     * Group chats user is in
     */
    private ArrayList<Integer> chats = new ArrayList<>();

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addChat(int chatId) {
        chats.add(chatId);
    }

    public void removeChat(int chatId) {
        chats.remove(chatId);
    }

    public ArrayList<Integer> getChats() {
        return this.chats;
    }

    @Override
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();

        obj.put("name", name);
        obj.put("id", id);

        return obj;
    }

    @Override
    public void fromJson(JSONObject obj) {
        name = obj.getString("name");
        id = obj.getInt("id");
    }
}
