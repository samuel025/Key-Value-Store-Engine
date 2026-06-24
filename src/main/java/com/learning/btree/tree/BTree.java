package com.learning.btree.tree;

import com.learning.btree.buffer.BufferPoolManager;
import com.learning.btree.buffer.Page;
import java.io.IOException;

/**
 * The core B+ Tree logic.
 */
public class BTree {
    private final BufferPoolManager bufferPool;
    private int rootPageId;

    public BTree(BufferPoolManager bufferPool, int rootPageId) throws IOException {
        this.bufferPool = bufferPool;
        this.rootPageId = rootPageId;
        

        if (this.rootPageId == -1) {
            Page rootPage = bufferPool.newPage();
            BTreeNode rootNode = new BTreeNode(rootPage, true);
            this.rootPageId = rootPage.getPageId();
        }
    }

    public int getRootPageId() {
        return rootPageId;
    }

    /**
     * Searches the tree for a key. Returns the value if found, or null if not found.
     */
    public Integer search(int key) throws IOException {
        // 1. Start at the root page
        int currentPageId = rootPageId;
        Page page = bufferPool.fetchPage(currentPageId);
        BTreeNode node = new BTreeNode(page);

        // 2. Traverse down the tree until we hit a Leaf Node
        while (!node.isLeaf()) {
            int i = 0;
            while (i < node.getNumKeys() && key >= node.getKey(i)) {
                i++;
            }
            currentPageId = node.getChild(i);
            
            page = bufferPool.fetchPage(currentPageId);
            node = new BTreeNode(page);
        }

        // 3. We are now at a Leaf Node! Scan its keys for an exact match.
        for (int i = 0; i < node.getNumKeys(); i++) {
            if (node.getKey(i) == key) {
                return node.getValue(i); 
            }
        }

        return null;
    }
}
