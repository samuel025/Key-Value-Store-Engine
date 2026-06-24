package com.learning.btree.tree;

import com.learning.btree.buffer.BufferPoolManager;
import com.learning.btree.storage.DiskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BTreeMassiveInsertTest {

    private static final String TEST_DB_FILE = "massive_test.db";
    private DiskManager diskManager;
    private BufferPoolManager bufferPool;

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(new File(TEST_DB_FILE).toPath());
        diskManager = new DiskManager(TEST_DB_FILE);
        bufferPool = new BufferPoolManager(diskManager, 50); // Give it some more RAM!
    }

    @AfterEach
    void tearDown() throws IOException {
        bufferPool.flushAllPages();
        diskManager.close();
        Files.deleteIfExists(new File(TEST_DB_FILE).toPath());
    }

    @Test
    void testMassiveInsertAndSplit() throws IOException {
        BTree tree = new BTree(bufferPool, -1);

        // Insert 10,000 keys!
        // Since a leaf node holds a maximum of 510 keys, this will force DOZENS of leaf splits,
        // and several internal node splits!
        System.out.println("Inserting 10,000 records into the database...");
        for (int i = 0; i < 10000; i++) {
            tree.insert(i, i * 10);
        }

        // Now let's ask the database to find some of them!
        System.out.println("Searching for records across the B-Tree...");
        assertEquals(50000, tree.search(5000), "Key 5000 should return 50000");
        assertEquals(99990, tree.search(9999), "Key 9999 should return 99990");
        assertEquals(0, tree.search(0), "Key 0 should return 0");
        
        System.out.println("MASSIVE TEST PASSED! The root page ID is now: " + tree.getRootPageId());
    }
}
