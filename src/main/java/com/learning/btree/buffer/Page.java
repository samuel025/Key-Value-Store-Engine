package com.learning.btree.buffer;

import com.learning.btree.storage.DiskManager;
import java.nio.ByteBuffer;

/**
 * Represents a single 4KB block of data sitting in memory.
 */
public class Page {
    private int pageId;
    private final byte[] data;
    // We use ByteBuffer to easily read/write primitives to the byte array
    private final ByteBuffer buffer; 
    
    // "Dirty" means the data in memory has been modified and no longer matches 
    // what is safely stored on the disk. It MUST be written to disk before being evicted!
    private boolean isDirty;
    
    public Page(int pageId) {
        this.pageId = pageId;
        this.data = new byte[DiskManager.PAGE_SIZE];
        this.buffer = ByteBuffer.wrap(this.data);
        this.isDirty = false;
    }

    // TODO: Write getters and setters for pageId and isDirty.
    // TODO: Write a getter for the `data` array so the DiskManager can read/write it.
    // TODO: Write a getter for the `buffer` so the B-Tree can read/write ints and doubles.
}
