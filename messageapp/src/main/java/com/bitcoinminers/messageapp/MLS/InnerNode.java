package com.bitcoinminers.messageapp.MLS;

public class InnerNode extends TreeNode{
    private TreeNode leftChild;
    private TreeNode rightChild;
    public InnerNode(int position, InnerNode parent, TreeNode sibling) {
        super(position, parent, sibling);
        leftChild = null;
        rightChild = null;
    }

    public void setLeftChild(TreeNode n) {
        leftChild = n;
    }

    public void setRightChild(TreeNode n) {
        rightChild = n;
    }
}
