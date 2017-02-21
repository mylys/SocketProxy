package com.sand;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class server {
    public static LogUtil mylog;
    public int listenport;
    public static String SumThread;
    public static String URL;
    public static String AUTH;
    public static String DoInfo;
    public static String uesrName;
    public static String password;
    public ServerSocketChannel serverSocketChannel ;
    public Selector selector;
    private String className;
    private String logpath;
    private String logname;
    public int count;
    public int timeout = 30;
    public int backlog=2;//最大传入等待队列数目。
    public ExecutorService es = null;
    private boolean isServe = false;
    private Map<String, Socket> socks = new HashMap<String, Socket>();

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public static String getSumThread() {
        return SumThread;
    }
 
    public static void setSumThread(String SumThread) {
        server.SumThread = SumThread;
    }
    
    
    public static String getUesrName() {
        return uesrName;
    }

    public static void setUesrName(String uesrName) {
        server.uesrName = uesrName;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        server.password = password;
    }

    public boolean isServe() {
        return isServe;
    }

    public void setServe(boolean isServe) {
        this.isServe = isServe;
    }

    public int getListenport() {
        return listenport;
    }

    public void setListenport(int listenport) {
        this.listenport = listenport;
    }

    public static String getURL() {
        return URL;
    }

    public static void setURL(String uRL) {
        URL = uRL;
    }

    public static String getAUTH() {
        return AUTH;
    }

    public static void setAUTH(String aUTH) {
        AUTH = aUTH;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getLogpath() {
        return logpath;
    }

    public void setLogpath(String logpath) {
        this.logpath = logpath;
    }

    public String getLogname() {
        return logname;
    }

    public void setLogname(String logname) {
        this.logname = logname;
    }

    public void start() {
        if (isServe)
            return;
        es = Executors.newFixedThreadPool(30);// 创建30个进程池。
        SimpleFormatter sf = new SimpleFormatter();
        FileHandler fh;

        try {
            mylog = new LogUtil(logname);
            mylog.setDirName(logpath);
            selector = Selector.open();              //Selector.open()方法创建一个Selector
            serverSocketChannel  = ServerSocketChannel.open();   //是一个可以监听新进来的TCP连接的通道
            serverSocketChannel.socket().setReuseAddress(true); //可以允许重复端口ip重复绑定
            serverSocketChannel.socket().bind(new InetSocketAddress(listenport), backlog);//backlog<1默认最大接入连接数为50，bug
            serverSocketChannel.configureBlocking(false);//非阻塞
            SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);// // 注册到selector，等待连接
            isServe = true;
            
            // 选择一组键，并且相应的通道已经打开
            while (selector.select() > 0) {
                // 返回此选择器的已选择键集。
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                
                while (iterator.hasNext()) {
                    SelectionKey selectkey = null;
                    try {
                        selectkey = (SelectionKey) iterator.next();
                        iterator.remove();
                        // 测试此键的通道是否已准备好接受新的套接字连接。
                        if (selectkey.isAcceptable()) {
                            // 返回为之创建此键的通道。
                            ServerSocketChannel serverSocketChannel  = (ServerSocketChannel) selectkey.channel();
                            // 接受到此通道套接字的连接。
                            // 此方法返回的套接字通道（如果有）将处于阻塞模式。
                            SocketChannel client = serverSocketChannel.accept();
                            // 配置为非阻塞
                            client.configureBlocking(false);
                            //设置超时时间
                            client.socket().setSoTimeout(timeout);
                            //设置接收buf内存大小
                            ByteBuffer recvBuf = ByteBuffer.allocate(1024 * 1024);
                            // 注册到selector，等待连接
                            client.register(selector, 1, recvBuf);
                            //break;
                        }else if (selectkey.isReadable()) {
                            //求JVM查找并加载指定的类， 也就是说JVM会执行该类的静态代码段
                            Class<?> c = Class.forName(className);//Worker工厂设计模式
                            Worker o = (Worker) c.newInstance();//newInstance通过反射创建对象
                            o.init(mylog, selector, URL, DoInfo, selectkey);//初始化传参
                            o.setTimeout(timeout);
                            es.execute((Runnable) o);//执行o中的run函数
                            selectkey.cancel();
                        }else if (selectkey.isValid()) {
                            selectkey.channel().close();
                            selectkey.cancel();
                        }
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        selectkey.cancel();

                    }
                }
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean stop() {
        try {
            isServe = false;
            es.shutdown();
            es = null;
            selector.close();
            selector = null;
            serverSocketChannel.close();
            serverSocketChannel = null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
