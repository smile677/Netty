package cn.smile67.netty.c1;

import lombok.extern.slf4j.Slf4j;


import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


/**
 * 使用ByteBuffer和Channel进行文件的读取
 */
@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {
        // FileChannel
        // 1.输入输出流 2.RandomAccessFile
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 准备缓存区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                // 从 channel 中读取数据，向 buffer 写入
                int len = channel.read(buffer);
                log.debug("实际字节数len:{}", len);
                if (len == -1) {
                    break;
                }
                // 打印 buffer 的内容
                buffer.flip();// 切换至读模式
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
                    log.debug("实际字节数b:{}", (char) b);
                }
                buffer.clear();// 切换至写模式// 清空 buffer，重置 position 和 limit
            }
        } catch (IOException e) {

        }

        // AI 生成
//        try {
//            FileChannel channel = FileChannel.open(Paths.get("1.txt"), StandardOpenOption.READ);
//            ByteBuffer buffer = ByteBuffer.allocate(10);
//            while (channel.read(buffer) != -1) {
//                buffer.flip();
//                while (buffer.hasRemaining()) {
//                    System.out.println((char) buffer.get());
//                }
//                buffer.clear();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
