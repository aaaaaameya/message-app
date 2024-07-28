package com.bitcoinminers.messageapp;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

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

    private HashMap<Integer, SecretKey> keys = new HashMap<>();

    public User(int id, String name) {
        this.id = id;
        this.name = name;
        System.out.printf("User %s created with id %d\n", name, id);
    }
    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addChat(int chatId, SecretKey s) {
        chats.add(chatId);
        keys.put(chatId, s);
    }

    public void removeChat(int chatId) {
        chats.remove(chatId);
    }

    public ArrayList<Integer> getChats() {
        return this.chats;
    }

    public void initialiseChat(int chatId) {
        try {
            chats.add(chatId);
            keys.put(chatId, EncryptionHelpers.generateAESKey());
            System.out.printf("%d has generated a new key for chat %d\n", id, chatId);
        } catch (NoSuchAlgorithmException e) {
            System.out.printf("Shouldn't get here: %s\n", e.getMessage());
        }
    }

    public SecretKey shareKey(int chatId) {
        byte key[] = keys.get(chatId).getEncoded();
        System.out.printf("Sending chat key insecurely: %s\n", key);
        return keys.get(chatId);
    }

    public void encryptMessage(int chatId, String rawMessage, Server server) {
        IvParameterSpec iv = EncryptionHelpers.generateIv();
        String ct = EncryptionHelpers.aesEncrypt(rawMessage, keys.get(chatId), iv);
        server.storeEncryptedMessage(chatId, name, ct, iv);
    }

    public Message decryptMessage(int chatId, Message m) {
        String pt = EncryptionHelpers.aesDecrypt(m.getContents(), keys.get(chatId), m.getIv());
        return new Message(m.getSender(), pt, m.getIv());
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
