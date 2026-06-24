package com.learning.btree.tree;

import com.learning.btree.buffer.Page;
import java.nio.ByteBuffer;

/**
 * Represents a single Node in our B-Tree.
 * This class acts as a wrapper around the `Page` object, translating raw bytes into arrays.
 */
public class BTreeNode {
    public static final int MAX_KEYS = 510;
    
    private final Page page;
    
    // Header
    private boolean isLeaf;
    private int numKeys;
    private int nextLeafPageId; 
    
    // Data arrays
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

    // Helper for testing until we implement the full B-Tree Insert Algorithm
    public void insertTemp(int key, int value) {
        if (!isLeaf) throw new IllegalStateException("Can only insert values into leaves!");
        keys[numKeys] = key;
        values[numKeys] = value;
        numKeys++;
        writeToPage(); // Pack it back into the buffer
    }
}
