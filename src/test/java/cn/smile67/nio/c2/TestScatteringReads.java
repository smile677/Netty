package cn.smile67.nio.c2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static cn.smile67.nio.c2.ByteBufferUtil.debugAll;

public class TestScatteringReads {
    public static void main(String[] args) {
        // FileChannel channel = new FileInputStream("data.txt").getChannel()
        try (FileChannel channel = new RandomAccessFile("words.txt", "r").getChannel()) {
            ByteBuffer b1 = ByteBuffer.allocate(3);
            ByteBuffer b2 = ByteBuffer.allocate(3);
            ByteBuffer b3 = ByteBuffer.allocate(5);
            channel.read(new ByteBuffer[]{b1, b2, b3}); // 从channel中读取数据到buffer中
//            b1.flip();
//            b2.flip();
//            b3.flip();
            debugAll(b1);
            debugAll(b2);
            debugAll(b3);
        } catch (IOException e) {
        }
    }
}
