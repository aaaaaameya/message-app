package com.bitcoinminers.messageapp;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/*
 * Uses SHA256 and PBKDF2 to implement a Ratchet for AES Keys
 * There is a factory for sending messages
 * Initialised when a user is added to a chat, and called to get a new key for each message
 */
public class Ratchet {
    private byte[] salt = {1};

    private SecretKeyFactory sendFactory = null;
    private SecretKey current;
    
    /*
     * A bit scuffed but split the secret into two halves to initialise the hash functions
     */
    public Ratchet(byte[] secret, int id) {
        try {
            sendFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            MessageDigest m = MessageDigest.getInstance("SHA-256");
            m.update(secret);
            m.update(ByteBuffer.allocate(4).putInt(id).array());
            char hash[] = new String(m.digest()).toCharArray();        
            KeySpec specs = new PBEKeySpec(hash, salt, 1024, 256);
            SecretKey newKey = sendFactory.generateSecret(specs);
            current = new SecretKeySpec(newKey.getEncoded(), "AES");
        } catch (Exception e) {
            System.err.println(e);
        }
    }


    /*
     * Get next key
     */
    public SecretKey nextKey() {
        try {
            SecretKey result = current;
            MessageDigest m = MessageDigest.getInstance("SHA-256");
            m.update(current.getEncoded());
            char hash[] = new String(m.digest()).toCharArray();
            KeySpec newSpecs = new PBEKeySpec(hash, salt, 1024, 256);
            SecretKey newKey = sendFactory.generateSecret(newSpecs);
            current = new SecretKeySpec(newKey.getEncoded(), "AES");
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String args[]) {
        byte b[] = {1, 2};
        Ratchet r = new Ratchet(b, 1);
        System.err.println(EncryptionHelpers.bytesToHexstring(r.nextKey().getEncoded()));
        System.err.println(EncryptionHelpers.bytesToHexstring(r.nextKey().getEncoded()));
        System.err.println(EncryptionHelpers.bytesToHexstring(r.nextKey().getEncoded()));
    }

}
