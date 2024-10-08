package cn.smile67.nio.c4;

import com.google.common.primitives.Chars;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));
        SocketAddress address = sc.getLocalAddress();
//        sc.write(Charset.defaultCharset().encode("hello\nworld\n"));
        sc.write(Charset.defaultCharset().encode("123456789abcdef3333\n"));
        sc.write(Charset.defaultCharset().encode("1234\n56789abcdef3333\n"));
        System.in.read();
//        System.out.println("waiting...");
//        sc.close();
    }
}