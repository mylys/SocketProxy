package com.sand;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;



public class mproxy3 implements Worker,Runnable{
	public static LogUtil mylog;
    Selector slt;
    static String URL;
    static String DoInfo;
    SelectionKey key = null;
 
	@Override
	public void init(LogUtil log, Selector s, String u, String info,
			SelectionKey k) {
		mylog = log;
		slt = s;
		URL=u;
		DoInfo = info;
		key = k;
	}

	@Override
	public void run() {
		ByteBuffer buff = (ByteBuffer)key.attachment();
	    SocketChannel sc = (SocketChannel)key.channel();
	    int ticker = 100;
	    SIO io = null;
	    buff.limit(buff.capacity());
	    buff.position(0);
	    int ilen = 0;
		try {
			String[] us = URL.split(":");
			io = new SIO(us[0],Integer.parseInt(us[1]));
			mylog.i("["+Thread.currentThread().toString()+"]start connect "+URL+io.getLastError());
			boolean error = false;
            while(true){
            int i = 0;
			if(io.isOk()){
				do{
					ticker --;
					if(ticker<=0){
						error = true;
						break;
					}
					buff.clear();
					ilen = sc.read(buff);
				    buff.flip();
					if(i==0&&ilen==0){
						Thread.sleep(1000);
						continue;
					}else if(i>0 && ilen == 0){
						break;
					}else if(ilen<0){
					mylog.i("connection close !");
					error=true;
					break;}
				    byte[] getdata = buff.array();
				    byte[] getbyte =  ByteUtil.sub(getdata, 0, ilen);
				    String httpreq = new String(getbyte);
				    String[] httpargs = httpreq.split("\r\n");
				    int postlen = 0;
				    boolean ipost = false;
				    for(String arg : httpargs){
				    	if(arg.contains("POST"))ipost = true;
				    	if(arg.contains("Content-Length")){
				    		String[] param = arg.split(":");
				    		postlen = Integer.parseInt(param[1].trim());
				    	}
				    }
					mylog.i("["+Thread.currentThread().toString()+"]read["+i+","+ilen+"]"+httpreq);
					i++;
					io.write(getbyte);
					if(ipost && postlen >0){
						while(postlen>0){
						buff.clear();
						ilen = sc.read(buff);
						buff.flip();
						getdata = buff.array();
					    getbyte =  ByteUtil.sub(getdata, 0, ilen);
					    mylog.i("["+Thread.currentThread().toString()+"]read["+i+","+ilen+"]"+new String(getbyte));
					    i++;
					    io.write(getbyte);
					    postlen = postlen - ilen;
					    if(ilen<=0)break;
						}
						break;
					}
					
					if(ilen<=0){
						error = true;
						break;
					}
				}while(true);
				if(error)break;
				i = 0;
				do{
					ticker --;
					if(ticker<=0){
						error = true;
						break;
					}
					try{
					byte[] tmp = io.read();
					if(tmp==null){
						error = true;
						break;
					}
					ByteBuffer mmbb = ByteBuffer.wrap(tmp);
					mylog.i("["+Thread.currentThread().toString()+"]send["+i+"]",tmp);
					mylog.i("["+Thread.currentThread().toString()+"]send["+i+"]"+new String(tmp));
					i++;
					ilen = sc.write(mmbb);
			        mylog.i( "send"+ilen);
						}catch (IOException e){
							mylog.i("["+Thread.currentThread().toString()+"]"+e.getLocalizedMessage());
							mylog.i("["+Thread.currentThread().toString()+"]"+e.getMessage());
//							error = true;
							break;
						}
				}while(true);
			}
			if(error)break;
            }
		}catch (Exception e) {
			mylog.i("["+Thread.currentThread().toString()+"]"+e.toString());
			mylog.i("["+Thread.currentThread().toString()+"]"+e.getLocalizedMessage());
			e.printStackTrace();
		}finally{
			try {
				if(sc!=null)sc.close();
				if(io!=null)io.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	
	
	@Override
	public byte[] doClient(byte[] getdata) throws IOException {
		mylog.i(new String(getdata));
		
		String[] us = URL.split(":");
		byte[] mre;
		
		SIO io = new SIO(us[0],Integer.parseInt(us[1]));
		mylog.i("["+Thread.currentThread().toString()+"]start connect "+URL+io.getLastError());
		if(io.isOk()){
			io.write(getdata);
			mre = io.read();
			do{
			try{
			byte[] tmp = io.read();
			if(tmp==null)break;
			mre = ByteUtil.union(mre, tmp);
				}catch (Exception e){
					mylog.i("["+Thread.currentThread().toString()+"]"+e.getLocalizedMessage());
					mylog.i("["+Thread.currentThread().toString()+"]"+e.getMessage());
					break;
				}

			}while(true);

			return mre;
		}
		return null;
	}

	@Override
	public void setTimeout(int timeout) {
		// TODO Auto-generated method stub
		
	}

}
