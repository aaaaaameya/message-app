package com.bitcoinminers.messageapp.MLS;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
// import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.generators.RSABlindingFactorGenerator;
// import org.bouncycastle.util.;
import org.bouncycastle.util.test.FixedSecureRandom;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.util.PrivateKeyFactory;

import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;



public class Experimentation {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        System.out.println(System.getProperty("java.class.path"));
        System.out.println("");

        byte[] seed = new byte[] { 0x01};

        FixedSecureRandom fs1 = new FixedSecureRandom(seed);
        FixedSecureRandom fs2 = new FixedSecureRandom(seed);
        System.out.println(fs1.getClass());
        
        // System.out.println(fs1.nextInt());
        // System.out.println(fs1.nextInt());
        // System.out.println(fs2.nextInt());
        // System.out.println(fs2.nextInt());
        
        // RSAKeyPairGenerator g = new RSAKeyPairGenerator();


        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048, fs1);

            KeyPair keys = keyPairGenerator.generateKeyPair();
            System.out.println(keys.getPublic().getClass().getSimpleName());
            System.out.println(keys.getPrivate());

            
    

            // System.out.println("");

            // keyPairGenerator.initialize(2048, fs2);
            // KeyPair keys2 = keyPairGenerator.generateKeyPair();
            // System.out.println(keys2.getPublic());
            // System.out.println(keys2.getPrivate());

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Use a specific algorithm parameter specification if needed
        // AlgorithmParameterSpec spec = new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4);

        // Initialize the key pair generator with the seed
      

        
        // SecureRandom s1 = new SecureRandom(seed);
        // SecureRandom s2 = new SecureRandom(seed);
        // System.err.println(s1.nextInt());
        // System.err.println(s1.nextInt());
        // System.err.println(s2.nextInt());
        // System.err.println(s2.nextInt());
        // Random r1 = new Random(1);
        // Random r2 = new Random(1);
        // System.err.println(r1.nextInt());
        // System.err.println(r1.nextInt());
        // System.err.println(r2.nextInt());
        // System.err.println(r2.nextInt());

    }


}
