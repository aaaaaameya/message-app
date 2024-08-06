package com.bitcoinminers.messageapp;

import javax.crypto.*;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

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

    private int treePosition;
    
    private ArrayList<Integer> chats = new ArrayList<>();
    private HashMap<Integer, ArrayList<Message>> chatLogs = new HashMap<>();

    private HashMap<Integer, KeyPair> DHKeys = new HashMap<>();
    private HashMap<Integer, Ratchet> ratchets = new HashMap<>();

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

    public void addChat(int chatId) {
        chats.add(chatId);
        chatLogs.put(chatId, new ArrayList<Message>());
    }

    public void removeChat(int chatId) {
        chats.remove(chatId);
    }

    public ArrayList<Integer> getChats() {
        return this.chats;
    }

    /*
     * As the first member of the chat, generate a local DH KeyPair for the chat
     */
    public void initialiseChat(int chatId) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(2048);
            DHKeys.put(chatId, kpg.generateKeyPair());
            System.out.printf("%d has generated a new key for chat %d\n", id, chatId);
        } catch (NoSuchAlgorithmException e) {
            System.out.printf("Shouldn't get here: %s\n", e.getMessage());
        }
    }

    /*
     * Phase 1 of a DH Exchange, public parameters and public key is shared
     */

    public byte[] initiateDH(int chatId) {
        return DHKeys.get(chatId).getPublic().getEncoded();
    }

    /*
     * Phase 2 of a DH Exchange performed by the receiver
     * Receives public params, calculates secret and returns its own public key
     */
    public byte[] acceptDH(int chatId, byte[] encodedPubKey) {
        try {
            PublicKey incomingPubKey = EncryptionHelpers.decodePublicKey(encodedPubKey);
            DHParameterSpec params = ((DHPublicKey)incomingPubKey).getParams();

            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(params);
            KeyPair kp = kpg.genKeyPair();
            DHKeys.put(chatId, kp);

            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(kp.getPrivate());
            ka.doPhase(incomingPubKey, true);
            byte[] sharedSecret = ka.generateSecret();
            ratchets.put(chatId, new Ratchet(sharedSecret, false));
            
            return kp.getPublic().getEncoded();
        } catch (Exception e) {
            return null;
        }
    }
    
    /*
     * Stage 3 of DH Exchange, performed by the sender
     * Receives public key from sender, calculates secret
     */
    public void completeDH(int chatId, byte[] encodedPubKey) {
        try {
            PublicKey incomingPubKey = EncryptionHelpers.decodePublicKey(encodedPubKey);
            
            KeyPair kp = DHKeys.get(chatId);
            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(kp.getPrivate());
            ka.doPhase(incomingPubKey, true);
            byte[] sharedSecret = ka.generateSecret();
            ratchets.put(chatId, new Ratchet(sharedSecret, true));
        } catch (Exception e) {
            // should not get here
        }
    }

    public ArrayList<Message> getMessages(int chatId) {
        return chatLogs.get(chatId);
    }

    /*
     * Fetch all existing messages from the server (encrypted)
     * Updates own storage to store decrypted messages locally
     * 
     * This is done prior to viewing logs or sending messages to ensure
     * ratchet is up to date.
     */
    public void pullMessages(int chatId, ArrayList<Message> messages) {
        try {
            for (int i = chatLogs.get(chatId).size(); i < messages.size(); i++) {
                chatLogs.get(chatId).add(decryptMessage(chatId, messages.get(i)));
            }
        } catch (GeneralSecurityException e) {
            System.err.println("Failed to decrypt messages");
        }
    }

    public void encryptMessage(int chatId, String rawMessage, Server server) {
        IvParameterSpec iv = EncryptionHelpers.generateIv();
        String ct = EncryptionHelpers.aesEncrypt(rawMessage, ratchets.get(chatId).nextSend(), iv);
        Message m = new Message(name, ct, iv);
        server.storeEncryptedMessage(chatId, m);
        chatLogs.get(chatId).add(new Message(name, rawMessage, iv));
    }

    public Message decryptMessage(int chatId, Message m) throws GeneralSecurityException {
        String pt = EncryptionHelpers.aesDecrypt(m.getContents(), ratchets.get(chatId).nextReceive(), m.getIv());
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
