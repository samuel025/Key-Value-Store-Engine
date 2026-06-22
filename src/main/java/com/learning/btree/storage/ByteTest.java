package com.learning.btree.storage;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteTest {
    public static void main(String[] args) {
        try{
            DiskManager diskManager = new DiskManager("my_first_db.db");
            diskManager.allocatePage();
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            buffer.putInt(40);
            buffer.putDouble(3.14);
            byte[] rawBytes = buffer.array();
            diskManager.writePage(0, rawBytes);
            byte[] freshArray = new byte[DiskManager.PAGE_SIZE];
            diskManager.readPage(0, freshArray);
            ByteBuffer readBuffer = ByteBuffer.wrap(freshArray);
            int myNumber = readBuffer.getInt();
            double myPi = readBuffer.getDouble();
            System.out.println("Read from disk: " + myNumber + ", " + myPi);
        } catch (IOException e){
            System.out.println(e);
        }

    }
}
