package com.bitcoinminers.messageapp;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.spec.IvParameterSpec;

import org.json.JSONObject;

/**
 * Group messaging chat.
 * @author Christian Albina
 */
public class GroupChat implements Saveable {

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

    /**
     * Users in the chat.
     */
    private ArrayList<Integer> users = new ArrayList<>();

    private HashMap<Integer, PublicKey> publicKeys = new HashMap<>();

    /**
     * Chat admins in the chat.
     */
    private ArrayList<Integer> admins = new ArrayList<>();

    public GroupChat(int id, String name) {
        this.id = id;
        this.name = name;
        System.out.printf("Chat %s created with id %d\n", name, id);
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

    public ArrayList<Integer> getUsers() {
        return users;
    }

    public void addMessage(Message m) {
        messages.add(m);
    }

    public void addUser(Integer userId) {
        users.add(userId);
    }
    
    public void removeUser(Integer userId) {
        users.remove(userId);
    }

    public boolean getAdminStatus(Integer userId) {
        for (int admin : admins) {
            if (userId == admin) return true;
        }
        return false;
    }

    public void makeAdmin(Integer userId) {
        admins.add(userId);
    }

    public void removeAdmin(Integer userId) {
        admins.remove(userId);
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
