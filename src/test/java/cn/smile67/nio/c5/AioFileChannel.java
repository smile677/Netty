package cn.smile67.nio.c5;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static cn.smile67.nio.c2.ByteBufferUtil.debugAll;

@Slf4j
public class AioFileChannel {
    public static void main(String[] args) throws IOException {
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("data.txt"), StandardOpenOption.READ)) {
            // 参数1 ByteBuffer
            // 参数2 读取的起始位置
            // 参数3 附件 ---> 一次读取不完时使用到
            // 参数4 回调函数
            ByteBuffer buffer = ByteBuffer.allocate(16);
            log.debug("开始读取");
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override // 读取成功
                public void completed(Integer result, ByteBuffer attachment) {
                    log.debug("读取成功");
                    attachment.flip();
                    debugAll(attachment);
                }

                @Override // 读取失败
                public void failed(Throwable exc, ByteBuffer attachment) {

                }
            }); // 守护线程
            log.debug("结束读取");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
