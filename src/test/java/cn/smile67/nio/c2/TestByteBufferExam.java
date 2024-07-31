package cn.smile67.nio.c2;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import static cn.smile67.nio.c2.ByteBufferUtil.debugAll;

public class TestByteBufferExam {
    public static void main(String[] args) {
         /*
         网络上有多条数据发送给服务端，数据之间使用 \n 进行分隔
         但由于某种原因这些数据在接收时，被进行了重新组合，例如原始数据有3条为
             Hello,world\n
             I'm zhangsan\n
             How are you?\n
         变成了下面的两个 byteBuffer (黏包，半包)
             Hello,world\nI'm zhangsan\nHo // 黏包: 两条数据被合并成一条   多条数据合成一条发造成的
             w are you?\n                  // 半包: 一条数据被拆分成了两条 缓冲区大小一定造成
         现在要求你编写程序，将错乱的数据恢复成原始的按 \n 分隔的数据
         */
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);
    }

    private static void split(ByteBuffer source) {
        source.flip();// 切换到读模式
        for (int i = 0; i < source.limit(); i++) {
            if(source.get(i) == '\n') { // get(i)方法不会移动指针
                int length  = i + 1 - source.position(); // i: 换行符索引
                ByteBuffer target = ByteBuffer.allocate(length);
                for (int j = 0; j < length; j++) {
                    target.put(source.get());// get() 会移动指针
                }
                debugAll(target);
            }
        }
        source.compact();// 压缩缓冲区，将未读的数据移到缓冲区头部  切换到写模式
    }
}
