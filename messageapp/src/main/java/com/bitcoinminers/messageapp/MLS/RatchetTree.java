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
    private ArrayList<TreeNode> tree = new ArrayList<>();
    private HashMap<Integer, Integer> userNodes = new HashMap<>();
    private TreeSet<Integer> isFree = new TreeSet();
    private Integer depth = 1;

    private byte[] salt = {1};

    public RatchetTree(ArrayList<Integer> users) {
        Integer size = 1;
        while (size < 2 * users.size()) size*=2;
        tree.ensureCapacity(size);
        for (Integer i = 0; i < users.size(); i++) {

        }
    }
    
    private void resize() {

    }

    // Insert into next free location or resize
    public void addUser(Integer userId) {
        if (isFree.isEmpty()) resize();
        Integer pos = isFree.iterator().next();
        isFree.remove(pos);
        pos = toTreeIndex(pos);
        LeafNode newNode = new LeafNode(pos, (InnerNode) tree.get(parentPos(pos)), tree.get(siblingPos(pos)));
        tree.add(pos, newNode);
        newNode.setupNewKey();
    }

    private Integer parentPos(Integer a) {
            return a/2;
    }

    private Integer siblingPos(Integer a) {
        int parentPos = parentPos(a);
        if (a == parentPos*2) return parentPos * 2 + 1;
        return parentPos*2;
    }


    private Integer toTreeIndex(Integer a) {
        String binStr = a.toBinaryString(depth.intValue());
        Integer index = 1;
        for (int i = 0; i < depth; i++) {
            // Print current character
            index *= 2;
            if (binStr.charAt(i) == '1') {
                index += 1;
            }
        }
        return index;
   }


    // 
    public void removeUser(Integer removedUser, Integer remover) {

    }

}
