package cn.smile67.nio.c2;

import java.nio.ByteBuffer;

public class TestByteBufferAllocate {
    public static void main(String[] args) {
        // allocate 不能动态调整
        System.out.println(ByteBuffer.allocate(16).getClass()); // HeapByteBuffer
        System.out.println(ByteBuffer.allocateDirect(16).getClass()); // DirectByteBuffer
        /*
            class java.nio.HeapByteBuffer    - java 堆内存，读写效率较低，受到 GC 的影响，数据会发生搬迁
            class java.nio.DirectByteBuffer  - 直接内存，读写效率高（少一次拷贝），不会受 GC 影响，
                                                要调用操作系统的函数分配内存的效率低，使用不恰当会造成内存泄露
         */
    }
}
