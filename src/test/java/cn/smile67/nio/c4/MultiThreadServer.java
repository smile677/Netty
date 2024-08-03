package cn.smile67.nio.c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.smile67.nio.c2.ByteBufferUtil.debugAll;

@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        // 1.创建固定数量的worker并优化
        Worker worker = new Worker("worker-0");
        while (true) {
            boss.select(); // 监听 selector 上面的事件
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
//                    new Worker("worker-0"); // 一个连接建立就创建一个worker很明显不行
                    log.debug("connected...{}", sc.getRemoteAddress());
                    // 2.关联 selector   worker.selector:Worker是静态内部类，所以可以通过对象实例来获取selector成员变量
                    log.debug("before...{}", sc.getRemoteAddress());
                    worker.register(sc); // 初始化线程，和selector 启动 worker-0 线程

                    log.debug("after...{}", sc.getRemoteAddress());
                }
            }
        }
    }

    // 检测读写事件
    static class Worker implements Runnable {
        private Thread thread;
        private Selector selector;
        private String name;
        private volatile/*保证可见性*/ boolean start = false; // 是否启动初始化

        public Worker(String name) {
            this.name = name;
        }

        // 初始化线程，和selector
        public void register(SocketChannel sc) throws IOException {
            if (!start) {
                selector = Selector.open();
                thread = new Thread(this, name);
                thread.start();
                start = true;// 保证只进行一次初始化
            }
            // 将 worker 里面的选择器 selector 跟 socketChannel绑定，目的是分工：boss只负责建立连接，worker只负责读写
            sc.register(selector, SelectionKey.OP_READ, null); // boss线程上执行 --->放在这里任然是在boss线程上执行的
        }

        // 检测读写事件
        @Override
        public void run() { // 问题出现在这儿
            while (true) {
                try {
                    selector.select();// 监听 selector 上面的事件
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            log.debug("read...{}", channel.getRemoteAddress());
                            channel.read(buffer);
                            buffer.flip();
                            debugAll(buffer);

                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
//    public static void main(String[] args) throws IOException {
//        Thread.currentThread().setName("boss");
//        ServerSocketChannel ssc = ServerSocketChannel.open();
//        ssc.configureBlocking(false);
//        Selector boss = Selector.open();
//        SelectionKey bossKey = ssc.register(boss, 0, null);
//        bossKey.interestOps(SelectionKey.OP_ACCEPT);
//        ssc.bind(new InetSocketAddress(8080));
//        // 1. 创建固定数量的 worker 并初始化
//        Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];
//        for (int i = 0; i < workers.length; i++) {
//            workers[i] = new Worker("worker-" + i);
//        }
//        AtomicInteger index = new AtomicInteger();
//        while (true) {
//            boss.select();// 监听 selector 上面的事件
//            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
//            while (iter.hasNext()) {
//                SelectionKey key = iter.next();
//                iter.remove();
//                if (key.isAcceptable()) {// 处理连接事件
//                    SocketChannel sc = ssc.accept();
//                    sc.configureBlocking(false);
//                    log.debug("connected...{}", sc.getRemoteAddress());
//                    // 2. 关联 selector
//                    log.debug("before register...{}", sc.getRemoteAddress());
//                    // round robin 轮询
//                    workers[index.getAndIncrement() % workers.length].register(sc); // boss 调用 初始化 selector , 启动 worker-0
//                    log.debug("after register...{}", sc.getRemoteAddress());
//                }
//            }
//        }
//    }
//
//    static class Worker implements Runnable {
//        private Thread thread;
//        private Selector selector;
//        private String name;
//        private volatile boolean start = false; // 还未初始化
//        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
//
//        public Worker(String name) {
//            this.name = name;
//        }
//
//        // 初始化线程，和 selector
//        public void register(SocketChannel sc) throws IOException {
//            if (!start) {
//                selector = Selector.open();
//                thread = new Thread(this, name);
//                thread.start();
//                start = true;
//            }
//            selector.wakeup(); // 唤醒 select 方法 boss
//            sc.register(selector, SelectionKey.OP_READ, null); // boss
//        }
//
//        @Override
//        public void run() {
//            while (true) {
//                try {
//                    selector.select(); // worker-0  阻塞
//                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
//                    while (iter.hasNext()) {
//                        SelectionKey key = iter.next();
//                        iter.remove();
//                        if (key.isReadable()) {
//                            ByteBuffer buffer = ByteBuffer.allocate(16);
//                            SocketChannel channel = (SocketChannel) key.channel();
//                            log.debug("read...{}", channel.getRemoteAddress());
//                            channel.read(buffer);
//                            buffer.flip();
//                            debugAll(buffer);
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
