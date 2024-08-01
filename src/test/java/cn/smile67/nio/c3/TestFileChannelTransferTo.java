package cn.smile67.nio.c3;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        try (
                FileChannel from = new FileInputStream("data.txt").getChannel();
                FileChannel to = new FileOutputStream("to2.txt").getChannel();
        ) {
            // 效率高，底层会利用操作系统的零拷贝进行优化, 2g 数据--->改进:多次传输
            long size = from.size();
            // left 变量代表还剩余多少字节
            for (long left = size; left > 0; ) {
                System.out.println("position:" + (size - left) + " left:" + left);
                long transferred = from.transferTo(size - left, left, to);
                left -= transferred;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
