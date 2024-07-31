package cn.smile67.nio.c2;

import java.nio.ByteBuffer;

import static cn.smile67.nio.c2.ByteBufferUtil.debugAll;

public class TestByteBufferRead {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip(); // position变成了0
        debugAll(buffer);

        // rewind 从同开始
//        buffer.get(new byte[4]);
//        debugAll(buffer);
//        buffer.rewind();
//        System.out.println((char) buffer.get()); // a

        // mark & reset
        // mark 做一个标记，记录position的位置，reset是将position重置到mark的位置
//        System.out.println((char) buffer.get()); // a
//        System.out.println((char) buffer.get()); // a
//        buffer.mark();
//        System.out.println((char) buffer.get()); // c
//        System.out.println((char) buffer.get()); // d
//        buffer.reset();
//        System.out.println((char) buffer.get()); // c
//        System.out.println((char) buffer.get()); // d

        // get(i) 不会改变索引的位置
        System.out.println((char) buffer.get(3)); // c
        debugAll(buffer);
    }
}
