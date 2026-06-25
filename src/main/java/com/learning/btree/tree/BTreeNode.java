package com.learning.btree.tree;

import com.learning.btree.buffer.Page;
import java.nio.ByteBuffer;

/**
 * Represents a single Node in our B-Tree.
 */
public class BTreeNode {
    public static final int MAX_KEYS = 510;
    
    private final Page page;
    
    private boolean isLeaf;
    private int numKeys;
    private int nextLeafPageId; 
    
    private final int[] keys = new int[MAX_KEYS];
    private final int[] values = new int[MAX_KEYS]; 
    private final int[] children = new int[MAX_KEYS + 1];

    /**
     * Creates a brand new, empty B+ Tree node.
     */
    public BTreeNode(Page page, boolean isLeaf) {
        this.page = page;
        this.isLeaf = isLeaf;
        this.numKeys = 0;
        this.nextLeafPageId = -1; // -1 means no next leaf
        writeToPage();
    }

    /**
     * Loads an existing B-Tree node from a Page.
     */
    public BTreeNode(Page page) {
        this.page = page;
        readFromPage();
    }

    /**
     * Parses the raw bytes from the page's buffer into our Java arrays.
     */
    private void readFromPage() {
        ByteBuffer buffer = page.getBuffer();
        
        buffer.rewind();
        
        isLeaf = (buffer.getInt() == 1);
        
        numKeys = buffer.getInt();
        
        nextLeafPageId=buffer.getInt();
        
        for (int i = 0; i < numKeys; i++) {
            keys[i] = buffer.getInt();
        }

        if(isLeaf == true){
            for (int i = 0; i < numKeys; i++) {
                values[i] = buffer.getInt();
            }
        } else {
            for (int i = 0; i < numKeys + 1; i++) {
                children[i] = buffer.getInt();
            }
        }
    }

    /**
     * Converts our Java arrays back into raw bytes to save to disk.
     */
    public void writeToPage() {
        ByteBuffer buffer = page.getBuffer();
        
        buffer.rewind();
        
        if (isLeaf == true) {
            buffer.putInt(1);
        } else {
            buffer.putInt(0);
        }
        
        buffer.putInt(numKeys);
        buffer.putInt(nextLeafPageId);
        
        for (int i = 0; i < numKeys; i++) {
            buffer.putInt(keys[i]);
        }
        
        if (isLeaf == true) {
            for (int i = 0; i < numKeys; i++) {
                buffer.putInt(values[i]);
            }
        } else {
            for (int i = 0; i < numKeys + 1; i++) {
               buffer.putInt(children[i]);
            }
        }
        page.setDirty(true);
    }

    public boolean isLeaf() { return isLeaf; }
    public int getNumKeys() { return numKeys; }
    public int getKey(int index) { return keys[index]; }
    public int getValue(int index) { return values[index]; }
    public int getChild(int index) { return children[index]; }
    /**
     * Inserts a key/value pair into a LEAF node in sorted order.
     */
    public void insertIntoLeaf(int key, int value) {
        if (!isLeaf) throw new IllegalStateException("Must be a leaf node!");
        if (numKeys >= MAX_KEYS) throw new IllegalStateException("Node is full!");
        
        int i = numKeys - 1;
        
        while (i >= 0 && keys[i] > key) {
            keys[i + 1] = keys[i];
            values[i + 1] = values[i];
            i--;
        }
        
        keys[i + 1] = key;
        values[i + 1] = value;
        numKeys++;
        
        writeToPage(); 
    }
    /**
     * Splits a full Leaf Node in half. 
     */
    public int splitLeaf(BTreeNode newRightSibling) {
        if (!isLeaf || !newRightSibling.isLeaf) {
            throw new IllegalStateException("Both nodes must be leaves!");
        }
        if (numKeys < MAX_KEYS) {
            throw new IllegalStateException("Node is not full!");
        }

        int mid = numKeys / 2; 
        int keysToMove = numKeys - mid;

        for (int i = 0; i < keysToMove; i++) {
            newRightSibling.keys[i] = this.keys[mid + i];
            newRightSibling.values[i] = this.values[mid + i];
        }

        newRightSibling.numKeys = keysToMove;
        this.numKeys = mid;

        newRightSibling.nextLeafPageId = this.nextLeafPageId;
        this.nextLeafPageId = newRightSibling.page.getPageId();

        this.writeToPage();
        newRightSibling.writeToPage();

        return newRightSibling.keys[0];
    }


    /**
     * Splits a full Internal Node in half.
     * Returns the "Middle Key" that needs to be pushed up to the parent.
     */
    public int splitInternal(BTreeNode newRightSibling) {
        if (isLeaf || newRightSibling.isLeaf) throw new IllegalStateException("Both nodes must be internal!");
        if (numKeys < MAX_KEYS) throw new IllegalStateException("Node is not full!");

        int mid = numKeys / 2; 
        int keysToMove = numKeys - mid - 1; 
        int middleKey = this.keys[mid];

        for (int i = 0; i < keysToMove; i++) {
            newRightSibling.keys[i] = this.keys[mid + 1 + i];
            newRightSibling.children[i] = this.children[mid + 1 + i];
        }

        newRightSibling.children[keysToMove] = this.children[numKeys];

        newRightSibling.numKeys = keysToMove;
        this.numKeys = mid; 

        this.writeToPage();
        newRightSibling.writeToPage();

        return middleKey;
    }

    /**
     * Inserts a routing key and child pointer into an INTERNAL node in sorted order.
     */
    public void insertIntoInternal(int key, int childPageId) {
        if (isLeaf) throw new IllegalStateException("Must be an internal node!");
        if (numKeys >= MAX_KEYS) throw new IllegalStateException("Node is full!");

        int i = numKeys - 1;
        
        while (i >= 0 && keys[i] > key) {
            keys[i + 1] = keys[i];
            children[i + 2] = children[i + 1]; 
            i--;
        }

        keys[i + 1] = key;
        children[i + 2] = childPageId;
        numKeys++;

        writeToPage();
        writeToPage();
    }
    
    public void setChild(int index, int childPageId) {
        children[index] = childPageId;
        writeToPage();
    }
    
    public int getPageId() { return page.getPageId(); }
}
