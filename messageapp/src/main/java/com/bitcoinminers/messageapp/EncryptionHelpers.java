package com.bitcoinminers.messageapp;

import javax.crypto.*;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;

import java.security.*;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
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

    public static PublicKey decodePublicKey(byte[] encodedPubKey) {
        try {
            KeyFactory kf = KeyFactory.getInstance("DH");
            X509EncodedKeySpec encSpec = new X509EncodedKeySpec(encodedPubKey);
            return kf.generatePublic(encSpec);
        } catch (Exception e) {
            return null;
        }
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String aesEncrypt(String pt, SecretKey s, IvParameterSpec iv) {
        try {
            System.err.println("Encrypting with:");
            System.err.println(bytesToHexstring(s.getEncoded()));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, s, iv);
            byte[] ciphertext = cipher.doFinal(pt.getBytes());
            return Base64.getEncoder().encodeToString(ciphertext);
        } catch (Exception e) {
            // should not reach here
            System.err.println(e);
            return null;
        }
    }
    
    public static String aesDecrypt(String ct, SecretKey s, IvParameterSpec iv) {
        try {
            System.err.println("Decrypting with:");
            System.err.println(bytesToHexstring(s.getEncoded()));
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

    // Yoinked from oracle
    public static void main(String args[]) {
        try {
        /*
         * Alice creates her own DH key pair with 2048-bit key size
         */
        System.out.println("ALICE: Generate DH keypair ...");
        KeyPairGenerator aliceKpairGen = KeyPairGenerator.getInstance("DH");
        aliceKpairGen.initialize(2048);
        KeyPair aliceKpair = aliceKpairGen.generateKeyPair();
        
        // Alice creates and initializes her DH KeyAgreement object
        System.out.println("ALICE: Initialization ...");
        KeyAgreement aliceKeyAgree = KeyAgreement.getInstance("DH");
        aliceKeyAgree.init(aliceKpair.getPrivate());
        
        // Alice encodes her public key, and sends it over to Bob.
        byte[] alicePubKeyEnc = aliceKpair.getPublic().getEncoded();
        
        /*
         * Let's turn over to Bob. Bob has received Alice's public key
         * in encoded format.
         * He instantiates a DH public key from the encoded key material.
         */
        KeyFactory bobKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(alicePubKeyEnc);

        PublicKey alicePubKey = bobKeyFac.generatePublic(x509KeySpec);

        /*
         * Bob gets the DH parameters associated with Alice's public key.
         * He must use the same parameters when he generates his own key
         * pair.
         */
        DHParameterSpec dhParamFromAlicePubKey = ((DHPublicKey)alicePubKey).getParams();

        // Bob creates his own DH key pair
        System.out.println("BOB: Generate DH keypair ...");
        KeyPairGenerator bobKpairGen = KeyPairGenerator.getInstance("DH");
        bobKpairGen.initialize(dhParamFromAlicePubKey);
        KeyPair bobKpair = bobKpairGen.generateKeyPair();

        // Bob creates and initializes his DH KeyAgreement object
        System.out.println("BOB: Initialization ...");
        KeyAgreement bobKeyAgree = KeyAgreement.getInstance("DH");
        bobKeyAgree.init(bobKpair.getPrivate());

        // Bob encodes his public key, and sends it over to Alice.
        byte[] bobPubKeyEnc = bobKpair.getPublic().getEncoded();

        /*
         * Alice uses Bob's public key for the first (and only) phase
         * of her version of the DH
         * protocol.
         * Before she can do so, she has to instantiate a DH public key
         * from Bob's encoded key material.
         */
        KeyFactory aliceKeyFac = KeyFactory.getInstance("DH");
        x509KeySpec = new X509EncodedKeySpec(bobPubKeyEnc);
        PublicKey bobPubKey = aliceKeyFac.generatePublic(x509KeySpec);
        System.out.println("ALICE: Execute PHASE1 ...");
        aliceKeyAgree.doPhase(bobPubKey, true);

        /*
         * Bob uses Alice's public key for the first (and only) phase
         * of his version of the DH
         * protocol.
         */
        System.out.println("BOB: Execute PHASE1 ...");
        bobKeyAgree.doPhase(alicePubKey, true);

        /*
         * At this stage, both Alice and Bob have completed the DH key
         * agreement protocol.
         * Both generate the (same) shared secret.
         */
        int bobLen;
        byte[] aliceSharedSecret = null;
        int aliceLen;
        byte[] bobSharedSecret = null;

        try {
            aliceSharedSecret = aliceKeyAgree.generateSecret();
            aliceLen = aliceSharedSecret.length;
            bobSharedSecret = new byte[aliceLen];
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }        // provide output buffer of required size
        bobLen = bobKeyAgree.generateSecret(bobSharedSecret, 0);
        System.out.println("Alice secret: " +
                bytesToHexstring(aliceSharedSecret));
        System.out.println("Bob secret: " +
                bytesToHexstring(bobSharedSecret));
        if (!java.util.Arrays.equals(aliceSharedSecret, bobSharedSecret))
            throw new Exception("Shared secrets differ");
        System.out.println("Shared secrets are the same");

        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        char[] c = "lol".toCharArray();
        byte[] salt = {1};
        KeySpec ks = new PBEKeySpec(c, salt, 1024, 128);
        SecretKeyFactory skf2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec ks2 = new PBEKeySpec(c, salt, 1024, 128);
        System.err.println(bytesToHexstring(skf.generateSecret(ks).getEncoded()));
        System.err.println(bytesToHexstring(skf2.generateSecret(ks2).getEncoded()));
            

        } catch (Exception e) {
            return;
        }
    }

}
