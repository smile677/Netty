package cn.smile67.nio.c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

import static cn.smile67.nio.c2.ByteBufferUtil.debugAll;
import static cn.smile67.nio.c2.ByteBufferUtil.debugRead;

@Slf4j
public class Server {
    private static void split(ByteBuffer source) {
        source.flip();// 切换到读模式
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i) == '\n') { // get(i)方法不会移动指针
                int length = i + 1 - source.position(); // i: 换行符索引
                ByteBuffer target = ByteBuffer.allocate(length);
                for (int j = 0; j < length; j++) {
                    target.put(source.get());// get() 会移动指针
                }
                debugAll(target);
            }
        }
        // 压缩缓冲区，将未读的数据移到缓冲区头部  切换到写模式
        source.compact();// 123456789abcdef position 16 limit 16
    }

    public static void main(String[] args) throws IOException {
        // 1. 创建 selector, 管理多个 channel
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 2. 建立 selector 和 channel 的联系（把channel注册到selector）
        // SelectionKey 就是将来事件发生后，通过它可以知道 事件 和哪个 channel 的事件

        /*
             事件类型：
             accept:serverSocket独有的事件，会在有连接请求是触发, <--- ServerSocketChannel关注的
             connect：是客户端的，连接连后触发,
             read：可读事件, <--- SocketChannel 关注的
             write：可写事件,
         */
        SelectionKey sscKey = ssc.register(selector, 0, null);// 0 表示不关注任何事件
        // key 只关注 accept 事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("sscKey:{}", sscKey);
        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            // 3. select 方法, 没有事件发生，线程阻塞，有事件，线程才会恢复运行--->解决了CPU空转问题
            // select 在事件未处理时，它不会阻塞, 事件发生后要么处理accept，要么取消cancel，不能置之不理 --->没出来事件造成可能的空转的原因
            selector.select();
            // 4. 处理事件, selectedKeys 内部包含了所有发生的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator(); // accept, read
            while (iter.hasNext()) { // 遍历的时候还想删除，得用迭代器
                SelectionKey key = iter.next();
                // 处理key的时候，要从selectedKeys集合中删除，否则下次处理还会处理到它，但是key上没有事件再调用方法会出现NPE
                iter.remove();// 这也是要用迭代器的原因，因为增强for循环不能在遍历的时候删除
                log.debug("key:{}", key);
                // 5. 区分事件类型
                if (key.isAcceptable()) { // 处理accept事件， 是ServerSocketChannel触发的
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept(); // 处理了事件，否则会发生空转
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    // 将一个 byteBuffer 作为附件关联到 SelectionKey 上
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}", sc);
//                key.cancel();
                } else if (key.isReadable()) { // SocketChannel才有read事件
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer); // 如果正常断开，read 返回-1
                        if (read == -1) {
                            key.cancel();
                        } else {
                            split(buffer);
                            if (buffer.position() == buffer.limit()) {
                                // 需要扩容
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer); // 123456789abcdef
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        // 因为客户端断开连接，产生读事件，不断会报错，所以捕获异常,将key取消（从 selector 的 keys 集合中真正删除 key）
                        key.cancel();//
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

