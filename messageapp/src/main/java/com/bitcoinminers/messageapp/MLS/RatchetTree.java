package com.bitcoinminers.messageapp.MLS;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class RatchetTree {
    private ArrayList<KeyPair> tree = new ArrayList<>();
    private HashMap<Integer, Integer> userNodes = new HashMap<>();
    private TreeSet<Integer> isFree = new TreeSet();

    private byte[] salt = {1};

    public RatchetTree(ArrayList<Integer> users) {
        int size = 1;
        while (size < 2 * users.size()) size*=2;
        tree.ensureCapacity(size);
        for (int i = 0; i < users.size(); i++) {

        }
    }
    
        private void resize() {
    
        }

    // Insert into next free location or resize
    public void addUser(int userId) {

    }

    // 
    public void removeUser(int removedUser, int remover) {

    }

}
