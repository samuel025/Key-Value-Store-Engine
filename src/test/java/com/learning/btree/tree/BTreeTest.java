package com.learning.btree.tree;

import com.learning.btree.buffer.BufferPoolManager;
import com.learning.btree.buffer.Page;
import com.learning.btree.storage.DiskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class BTreeTest {

    private static final String TEST_DB_FILE = "btree_test.db";
    private DiskManager diskManager;
    private BufferPoolManager bufferPool;

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(new File(TEST_DB_FILE).toPath());
        diskManager = new DiskManager(TEST_DB_FILE);
        bufferPool = new BufferPoolManager(diskManager, 10);
    }

    @AfterEach
    void tearDown() throws IOException {
        bufferPool.flushAllPages();
        diskManager.close();
        Files.deleteIfExists(new File(TEST_DB_FILE).toPath());
    }

    @Test
    void testBTreeSearch() throws IOException {
        // 1. Initialize a brand new BTree. This creates the root Leaf Node at Page 0.
        BTree tree = new BTree(bufferPool, -1);
        int rootPageId = tree.getRootPageId();
        assertEquals(0, rootPageId, "First page allocated should be 0");

        // 2. Fetch the root page and manually inject some data
        Page rootPage = bufferPool.fetchPage(rootPageId);
        BTreeNode rootNode = new BTreeNode(rootPage);
        
   

        // 3. Search the tree!
        Integer result1 = tree.search(42);
        Integer result2 = tree.search(100);
        Integer result3 = tree.search(999); 
        // 4. Assert the results
        assertEquals(99, result1, "Search for 42 should return 99");
        assertEquals(500, result2, "Search for 100 should return 500");
        assertNull(result3, "Search for non-existent key should return null");
        
        System.out.println("Test passed! Our Storage Engine successfully retrieved the data from disk.");
    }
}
