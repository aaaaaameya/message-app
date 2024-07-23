package com.bitcoinminers.messageapp;

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

    /*
     * Admin status of user.
     */
    private boolean isAdmin = false;

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

    public boolean getAdminStatus() {
        return isAdmin;
    }

    public void toggleAdminStatus() {
        this.isAdmin = !this.isAdmin;
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
