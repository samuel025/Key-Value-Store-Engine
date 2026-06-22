package com.learning.btree.buffer;

import com.learning.btree.storage.DiskManager;
import com.learning.btree.buffer.Page;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manages the in-memory cache of pages.
 */
public class BufferPoolManager {
    private final DiskManager diskManager;
    private final int capacity;
    
    // We use a LinkedHashMap to easily implement an LRU (Least Recently Used) Cache!
    private final Map<Integer, Page> pageTable;

    public BufferPoolManager(DiskManager diskManager, final int capacity) {
        this.diskManager = diskManager;
        this.capacity = capacity;
        
        this.pageTable = new LinkedHashMap<Integer, Page>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Page> eldest) {
                if (size() > capacity) {
                    evict(eldest.getValue());
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * Fetches a page from the buffer pool. If it's not in RAM, read it from disk.
     */
    public Page fetchPage(int pageId) throws IOException {
        if (pageTable.containsKey(pageId)) {
            return pageTable.get(pageId);
        }
        
        Page page = new Page(pageId);
        
        diskManager.readPage(pageId, page.getData());
        
        pageTable.put(pageId, page);
        
        return page;
    }

    /**
     * Allocates a brand new page on disk, and puts it in the buffer pool.
     */
    public Page newPage() throws IOException {
        int PageId = diskManager.allocatePage();
        Page page = new Page(PageId);
        pageTable.put(PageId, page);
        return page;
    }

    /**
     * This is called automatically when the cache is full and we need to kick a page out.
     */
    private void evict(Page page) {

        if (page.isDirty()) {
            try {
                diskManager.writePage(page.getPageId(), page.getData());
            } catch (IOException e) {
                throw new RuntimeException("");
            }
        }
    }
    
    /**
     * Flushes all dirty pages to disk (useful when shutting down the database).
     */
    public void flushAllPages() throws IOException {
        for (Page page : pageTable.values()) {
            if (page.isDirty()) {
                diskManager.writePage(page.getPageId(), page.getData());
                page.setDirty(false);
            }
        }
    }
}
