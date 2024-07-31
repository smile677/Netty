package cn.smile67.nio.c2;


import java.nio.ByteBuffer;

import static cn.smile67.nio.c2.ByteBufferUtil.debugAll;

public class TestByteBufferReadWrite {
    public static void main(String[] args) {
        // 写入
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61);// 'a'
        debugAll(buffer);
        buffer.put(new byte[]{0x62, 0x63, 0x64}); // b c d
        debugAll(buffer);
//        System.out.println(buffer.get()); // 0
        // 读取
        buffer.flip();
        System.out.println(buffer.get());// 97
        debugAll(buffer);
        buffer.compact();
        debugAll(buffer);
        // 压缩后写入
        buffer.put(new byte[]{0x65, 0x66});
        debugAll(buffer);
    }
}
