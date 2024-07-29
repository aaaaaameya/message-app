package com.bitcoinminers.messageapp;

import java.security.MessageDigest;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Ratchet {
    private byte[] salt = {1};

    private MessageDigest sendDigest = null;
    private MessageDigest receiveDigest = null;

    private SecretKeyFactory sendRatchet = null;
    private SecretKeyFactory receiveRatchet = null;
    
    public Ratchet(byte[] secret, boolean isSender) {
        try {
            sendDigest = MessageDigest.getInstance("SHA-256");
            receiveDigest = MessageDigest.getInstance("SHA-256");
            sendRatchet = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            receiveRatchet = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            
            int length = secret.length;
            byte[] b1 = new byte[length / 2];
            byte[] b2 = new byte[length / 2];
            for (int i = 0; i < length/2; i++) {
                b1[i] = secret[i];
            }
            for (int i = 0; i < length/2; i++) {
                b2[i] = secret[i + length/2];
            }
            
            if (isSender) {
                sendDigest.update(b1);
                receiveDigest.update(b2);
            } else {
                sendDigest.update(b2);
                receiveDigest.update(b1);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }


    public SecretKey nextSend() {
        try {
            char[] prev = new String(sendDigest.digest()).toCharArray();
            KeySpec specs = new PBEKeySpec(prev, salt, 1024, 256);
            byte[] s = sendRatchet.generateSecret(specs).getEncoded();
            sendDigest.update(s);
            SecretKey key = new SecretKeySpec(s, "AES");
            return key;
        } catch (Exception e) {
            return null;
        }
    }
    
    public SecretKey nextReceive() {
        try {
            char[] prev = new String(receiveDigest.digest()).toCharArray();
            KeySpec specs = new PBEKeySpec(prev, salt, 1024, 256);
            byte[] s = receiveRatchet.generateSecret(specs).getEncoded();
            receiveDigest.update(s);
            SecretKey key = new SecretKeySpec(s, "AES");
            return key;
        } catch (Exception e) {
            return null;
        }
    }

}
