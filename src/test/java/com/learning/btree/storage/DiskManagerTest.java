package com.learning.btree.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DiskManagerTest {

    private static final String TEST_DB_FILE = "test.db";
    private DiskManager diskManager;

    @BeforeEach
    void setUp() throws IOException {
        // Run before every test: clean up any old file and create a fresh DiskManager
        Files.deleteIfExists(new File(TEST_DB_FILE).toPath());
        diskManager = new DiskManager(TEST_DB_FILE);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Run after every test: close the file and delete it
        diskManager.close();
        Files.deleteIfExists(new File(TEST_DB_FILE).toPath());
    }

    @Test
    void testReadAndWritePage() throws IOException {
        int pageId = diskManager.allocatePage();
        assertEquals(0, pageId, "First allocated page should be 0");

        // Create some dummy data (a 4KB array filled with 7s)
        byte[] writeBuffer = new byte[DiskManager.PAGE_SIZE];
        Arrays.fill(writeBuffer, (byte) 7);

        // Write to disk
        diskManager.writePage(pageId, writeBuffer);

        // Now read it back into a fresh array
        byte[] readBuffer = new byte[DiskManager.PAGE_SIZE];
        diskManager.readPage(pageId, readBuffer);

        // Verify that what we read matches what we wrote!
        assertArrayEquals(writeBuffer, readBuffer, "Read data should match written data");
    }

    @Test
    void testMultiplePages() throws IOException {
        int page0 = diskManager.allocatePage();
        int page1 = diskManager.allocatePage();

        byte[] buffer0 = new byte[DiskManager.PAGE_SIZE];
        Arrays.fill(buffer0, (byte) 1);
        
        byte[] buffer1 = new byte[DiskManager.PAGE_SIZE];
        Arrays.fill(buffer1, (byte) 2);

        diskManager.writePage(page0, buffer0);
        diskManager.writePage(page1, buffer1);

        byte[] readBuffer = new byte[DiskManager.PAGE_SIZE];
        
        diskManager.readPage(page1, readBuffer);
        assertEquals((byte) 2, readBuffer[0], "Page 1 should be filled with 2s");

        diskManager.readPage(page0, readBuffer);
        assertEquals((byte) 1, readBuffer[0], "Page 0 should be filled with 1s");
    }
}
