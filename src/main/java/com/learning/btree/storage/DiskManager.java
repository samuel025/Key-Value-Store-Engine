package com.learning.btree.storage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Handles all raw disk I/O.
 */
public class DiskManager {
    public static final int PAGE_SIZE = 4096;
    
    private final RandomAccessFile file;
    private int nextPageId; 

    /**
     * Opens or creates a database file.
     */
    public DiskManager(String filePath) throws IOException {
        File dbFile = new File(filePath);    
        this.file = new RandomAccessFile(dbFile, "rw");
        this.nextPageId = (int) (file.length() / PAGE_SIZE);
    }

    /**
     * Reads a 4KB page from disk into the provided byte array.
     */
    public void readPage(int pageId, byte[] pageData) throws IOException {
        if (pageData.length != PAGE_SIZE) {
            throw new IllegalArgumentException("Byte array must be exactly " + PAGE_SIZE + " bytes");
        }
        long offset = (long) pageId * PAGE_SIZE;
        file.seek(offset);
        file.readFully(pageData);
    }

    /**
     * Writes a 4KB byte array back to a specific page on disk.
     */
    public void writePage(int pageId, byte[] pageData) throws IOException {
        if (pageData.length != PAGE_SIZE){
            throw new IllegalArgumentException("Byte array must be exactly " + PAGE_SIZE + " bytes");
        }
        long offset = (long) pageId * PAGE_SIZE;
        file.seek(offset);
        file.write(pageData);
    }

    /**
     * Allocates a new empty page at the end of the file.
     * @return The newly assigned pageId.
     */
    public int allocatePage() {
        int newPageId = nextPageId;
        nextPageId++;
        return newPageId;
    }
    
    public void close() throws IOException {
        file.close();
    }
}
