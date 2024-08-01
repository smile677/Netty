package cn.smile67.nio.c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

import static cn.smile67.nio.c2.ByteBufferUtil.debugRead;

@Slf4j
public class Server {
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
                log.debug("key:{}", key);
                // 5. 区分事件类型
                if (key.isAcceptable()) { // 处理accept事件， 是ServerSocketChannel触发的
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept(); // 处理了事件，否则会发生空转
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}", sc);
//                key.cancel();
                } else if (key.isReadable()) { // SocketChannel才有read事件
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    channel.read(buffer);
                    buffer.flip();
                    debugRead(buffer);
                }
            }
        }
    }
}

