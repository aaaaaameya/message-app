package com.bitcoinminers.messageapp;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.json.JSONObject;

/**
 * User of message-app. Simulates an individual device (such as a
 * personal phone).
 * @author Christian Albina
 */
public class User implements Saveable {
    
    /*
     * Number of messages before secret should be reset, including own keypair
     */
    private static int RESET_INTERVAL = 3;

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

    private Server server;

    
    private ArrayList<Integer> chats = new ArrayList<>();
    private HashMap<Integer, TreeMap<Integer, Message>> chatLogs = new HashMap<>();


    private HashMap<Integer, PublicKey> selfGroupChatPublicKeys = new HashMap<>();
    private HashMap<Integer, PrivateKey> selfGroupChatPrivateKeys = new HashMap<>();

    // chat id -> user id to get the AES key of other user in that chat 
    private HashMap<Integer, HashMap<Integer, Ratchet>> senderKeys = new HashMap<>();

    public User(int id, String name, Server server) {
        this.id = id;
        this.name = name;
        this.server = server;
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
        if (chatLogs.get(chatId) == null) chatLogs.put(chatId, new TreeMap<Integer, Message>());
    }

    public void removeChat(int chatId) {
        chats.remove(chatId);
    }

    public ArrayList<Integer> getChats() {
        return this.chats;
    }

    public void joinGroupChat(Chat chat) {
        chats.add(chat.getId());
        try {
            generateKeyPair(chat);
            if (senderKeys.get(chat.getId()) == null) {
                HashMap<Integer, Ratchet> keys = new HashMap<>();
                keys.put(id, null);
                senderKeys.put(chat.getId(), keys);
            }
            generateGroupSecret(chat);
            
        } catch (Exception e) {
            System.out.printf("Shouldn't get here: %s\n", e.getMessage());
        }
    }
    
    public SecretKey generateGroupSecret(Chat chat) {
        try {
            //generate new secret
            SecretKey groupSecret = EncryptionHelpers.generateAESKey();
            
            // send new secret to each member of the chat encrypting it with their public key
            HashMap<Integer, PublicKey> groupsPks =  chat.getUserPublicKeys();
        
            HashMap<Integer, byte[]> broadcast = new HashMap<>();
            for (Integer userId: chat.getUsers()) {
                PublicKey pk = groupsPks.get(userId);
                byte[] encryptedSecret =  EncryptionHelpers.RSAEncryptSK(pk, groupSecret);
                broadcast.put(userId, encryptedSecret);
            }
            // send
            server.ping(chat.getId(), broadcast);
            System.out.printf("Successfully renewed secret for chat %d\n", chat.getId());
            return groupSecret;
        } catch (Exception e) {
            System.out.printf("Shouldn't get here: %s\n", e.getMessage());
            return null;
        }
    }

    public void computeSenderKeys(Chat chat, SecretKey secret) { 
        pullMessages(chat.getId(), chat.getMessages());
        try {
            HashMap<Integer, Ratchet> groupsSenderKeys = senderKeys.get(chat.getId());

            for (Integer userId: chat.getUsers()) {
                Ratchet r = new Ratchet(secret.getEncoded(), userId);
                groupsSenderKeys.put(userId, r);
            }
        } catch (Exception e) {
            System.out.printf("Shouldn't get here: %s\n", e.getMessage());
        }
    }

    public void receivePing(Chat chat, byte[] encryptedNewSecret) throws Exception {
        try {
            PrivateKey privKey = selfGroupChatPrivateKeys.get(chat.getId());
            SecretKey newSecret = EncryptionHelpers.RSADecryptSK(privKey, encryptedNewSecret);
            computeSenderKeys(chat, newSecret);
        } catch (Exception e) {
            System.err.println(e);
        }    
    }

    public ArrayList<Message> getMessages(int chatId) {
        return new ArrayList<Message>(chatLogs.get(chatId).values());
    }

    /*
     * Fetch all existing messages from the server (encrypted)
     * Updates own storage to store decrypted messages locally
     * 
     * This is done prior to viewing logs or sending messages to ensure
     * ratchet is up to date.
     */
    public void pullMessages(int chatId, ArrayList<Message> messages) {
        TreeMap<Integer, Message> logs = chatLogs.get(chatId);
        for (int i = 0; i < messages.size(); i++) {
            if (!logs.containsKey(i)) {    
                chatLogs.get(chatId).put(i, decryptMessage(chatId, messages.get(i)));
            }
        }
    }

    public void encryptMessage(int chatId, String rawMessage, Server server) {
        if (chatLogs.get(chatId).size() % RESET_INTERVAL == 0) {   
            generateGroupSecret(server.getChat(chatId));
        }
        IvParameterSpec iv = EncryptionHelpers.generateIv();
        Ratchet selfRatchet = senderKeys.get(chatId).get(id);
        String ct = EncryptionHelpers.aesEncrypt(rawMessage, selfRatchet.nextKey(), iv);
        Message m = new Message(id, name, ct, iv);
        int messageNum = server.storeEncryptedMessage(chatId, m);
        chatLogs.get(chatId).put(messageNum, new Message(id, name, rawMessage, iv));
    }
    
    public Message decryptMessage(int chatId, Message m) {
        try {
            Ratchet senderRatchet = senderKeys.get(chatId).get(m.getSenderId());
            String pt = EncryptionHelpers.aesDecrypt(m.getContents(), senderRatchet.nextKey(), m.getIv());
            m.getSender();
            return new Message(m.getSenderId(), m.getSender(), pt, m.getIv());
        } catch (Exception e) {
            // System.err.printf("Failed to decrypt message from %d. Storing encrypted message instead.\n", m.getSenderId());
            return m;
        }
    }

    private void generateKeyPair(Chat chat) {
        try {
            //generate RSA pair
            KeyPair rsaKeys = EncryptionHelpers.generateRSAKeyPair();
            selfGroupChatPrivateKeys.put(Integer.valueOf(chat.getId()), rsaKeys.getPrivate());
            selfGroupChatPublicKeys.put(Integer.valueOf(chat.getId()), rsaKeys.getPublic());
            chat.addUserPublicKeys(id, rsaKeys.getPublic());
        } catch (Exception e) {
            return;
        }
    }

    public void stopCompromise() {
        for (int chatId : chats) {
            Chat chat = server.getChat(chatId);
            generateKeyPair(chat);
            generateGroupSecret(chat);
        }
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
