package com.learning.btree.buffer;

import com.learning.btree.storage.DiskManager;
import java.nio.ByteBuffer;

/**
 * Represents a single 4KB block of data sitting in memory.
 */
public class Page {
    private int pageId;
    private final byte[] data;
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

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    public byte[] getData() {
        return data;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
