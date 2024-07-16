package com.bitcoinminers.messageapp;

import org.json.JSONObject;

/**
 * Simple message container.
 * @author Christian Albina
 */
public class Message implements Saveable {
    /**
     * Name of the sender of the message.
     */
    private String sender;

    /**
     * Message contents.
     */
    private String contents;

    public Message(String sender, String contents) {
        this.sender = sender;
        this.contents = contents;
    }

    @Override
    public String toString() {
        return String.format("%s: %s\n", sender, contents);
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
