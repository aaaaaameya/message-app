package com.bitcoinminers.messageapp.MLS;
import java.security.NoSuchAlgorithmException;
import com.bitcoinminers.messageapp.EncryptionHelpers;


import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class LeafNode extends TreeNode {


    public LeafNode(int treePosition, InnerNode parent, TreeNode sibling) {
        super(treePosition, parent, sibling);
    }


    public void setupNewKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            setKeys(keyPairGenerator.generateKeyPair());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        TreeNode parentNode = getParent();
        parentNode.ratchetUpKeys(getKeys());
    }

}
