package com.bitcoinminers.messageapp;

import javax.crypto.spec.IvParameterSpec;

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

    /**
     * IV for message
     */
    private IvParameterSpec iv;

    public Message(String sender, String contents, IvParameterSpec iv) {
        this.sender = sender;
        this.contents = contents;
        this.iv = iv;
    }

    public String getSender() {
        return sender;
    }

    public String getContents() {
        return contents;
    }

    public IvParameterSpec getIv() {
        return iv;
    }

    @Override
    public String toString() {
        return String.format("%s (IV: %s): %s\n", sender, EncryptionHelpers.bytesToHexstring(iv.getIV()), contents);
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
