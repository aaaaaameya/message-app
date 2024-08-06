package com.bitcoinminers.messageapp.MLS;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.MessageDigest;
import java.util.Random;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.test.FixedSecureRandom;



public class Experimentation {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        System.out.println(System.getProperty("java.class.path"));
        System.out.println("");

        byte[] seed = new byte[] { 0x00, 0x00, 0x00, 0x01, 0x02, 0x02, 0x03, 0x04,  0x01, 0x02, 0x03, 0x04,  0x01, 0x02, 0x03, 0x04};

        FixedSecureRandom fs1 = new FixedSecureRandom(seed);
        FixedSecureRandom fs2 = new FixedSecureRandom(seed);
        System.out.println(fs1.getClass());
        
        System.out.println(fs1.nextInt());
        System.out.println(fs1.nextInt());
        System.out.println(fs2.nextInt());
        System.out.println(fs2.nextInt());
        
        
        
        SecureRandom s1 = new SecureRandom(seed);
        SecureRandom s2 = new SecureRandom(seed);
        System.err.println(s1.nextInt());
        System.err.println(s1.nextInt());
        System.err.println(s2.nextInt());
        System.err.println(s2.nextInt());
        Random r1 = new Random(1);
        Random r2 = new Random(1);
        System.err.println(r1.nextInt());
        System.err.println(r1.nextInt());
        System.err.println(r2.nextInt());
        System.err.println(r2.nextInt());

    }
}
