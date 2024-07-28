package com.bitcoinminers.messageapp;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

import java.security.*;
import java.util.Base64;

public class EncryptionHelpers {

    public static String bytesToHexstring(byte[] bytes) {
        String res = "0x";
        for (byte b : bytes) {
            res += String.format("%02x", b);
        }
        return res;
    }

    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        return kg.generateKey();
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String aesEncrypt(String pt, SecretKey s, IvParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, s, iv);
            byte[] ciphertext = cipher.doFinal(pt.getBytes());
            return Base64.getEncoder().encodeToString(ciphertext);
        } catch (Exception e) {
            // should not reach here
            System.err.println(e);
            return pt;
        }
    }
    
    public static String aesDecrypt(String ct, SecretKey s, IvParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, s, iv);
            byte[] plaintext = cipher.doFinal(Base64.getDecoder().decode(ct));
            return new String(plaintext);
        } catch (Exception e) {
            //  should not reach here
            System.err.println(e);
            return ct;
        }
    }

    public static void main(String args[]) {
        byte[] b = {1, 2, 3, 4, 70};
        System.out.println(bytesToHexstring(b));
    }

}
