package com.sand;
//java -jar mproxy.jar [listenport]  [URL:PORT] [LOGNAME] [GZIP/SN] [TIMEOUT]
//test comments
public class App {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("java -jar mproxy.jar [listenport]  [URL:PORT] [LOGNAME] [GZIP/SN] [TIMEOUT]");
            return;
        }
        for (int i = 0; i < args.length; i++)
            System.out.println("-->" + args[i]);
        server server = new server();
        server.setListenport(Integer.parseInt(args[0]));
        server.setURL(args[1]);
        ///System.out.println("getenv HOME" + System.getenv("HOME"));
        //System.out.println("getenv HOME" + System.getProperty("user.dir"));

        if (System.getenv("LOGPATH") != null) {
            server.setLogpath(System.getenv("LOGPATH"));
        } else {
            server.setLogpath(System.getProperty("user.dir") + "/log");
        }
        System.out.println("UseLogPath:" + System.getProperty("user.dir") + "/log");

        server.setLogname(args[2]);
        if (args.length >= 4) {
            if (args[3].equalsIgnoreCase("SN")) {
                server.setClassName("com.sand.mproxy2");
                if (args.length == 5) {
                    server.setTimeout(Integer.parseInt(args[4]));
                }
            } else
                server.setClassName("com.sand.mproxy2");//定义使用哪一个类
        } else
            server.setClassName("com.sand.mproxy2");
        System.out.println("UseClass:"+server.getClassName());
        server.start();// 调用类server.java 中的 public class server
    }
}
