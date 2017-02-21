package com.sand;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;



public class mproxy4 implements Worker,Runnable{
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
		mylog.i("["+Thread.currentThread().toString()+"]start a new client!");
		ByteBuffer buff = (ByteBuffer)key.attachment();
	    SocketChannel sc = (SocketChannel)key.channel();
	    
	    buff.limit(buff.capacity());
	    buff.position(0);
	    int ilen = 0;
		try {
			byte[] getdata;
			byte[] getbyte = null;
			while(true){
			ilen = sc.read(buff);
		    buff.flip();
		    getdata = buff.array();
		    if(getbyte == null)getbyte =  ByteUtil.sub(getdata, 0, ilen);
		    else getbyte =  ByteUtil.union(getbyte, ByteUtil.sub(getdata, 0, ilen));
		    if(ilen<=0)break;
			}
		    mylog.i("["+Thread.currentThread().toString()+"]read=",getbyte);
	        byte[] resend = doClient(getbyte);
	        mylog.i("["+Thread.currentThread().toString()+"]send=",resend);
	        ByteBuffer mmbb = ByteBuffer.wrap(resend);
	        mylog.i(new String(resend));
	        do{
	        ilen = sc.write(mmbb);
	        mylog.i( "send"+ilen);
	        }while(ilen>0);
		}catch (Exception e) {
			mylog.i("["+Thread.currentThread().toString()+"]"+e.getMessage());
			mylog.i("["+Thread.currentThread().toString()+"]"+e.getLocalizedMessage());
			e.printStackTrace();
		}finally{
			try {
				if(sc!=null)sc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	@Override
	public byte[] doClient(byte[] getdata) throws IOException {
		String http = new String(getdata);
		if(!http.endsWith("\r\n")){
			getdata = ByteUtil.union(getdata, new byte[]{0x0d,0x0a});
		}
		
//		if(!http.contains("suning")){
//			http="HTTP/1.1 400 Bad Request\r\nServer: Apache-Coyote/1.1\r\nTransfer-Encoding: chunked\r\nDate: Mon, 20 Oct 2014 04:26:09 GMT\r\nConnection: close\r\n\r\n0\r\n\r\n";
//			return http.getBytes();
//		}
		
		mylog.i(http);
		
		String[] us = URL.split(":");
		byte[] mre;
		
		SIO io = new SIO(us[0],Integer.parseInt(us[1]));
		mylog.i("start connect "+URL);
		if(io.isOk()){
			io.write(getdata);
			mre = io.read();
			do{
			try{
			byte[] tmp = io.read();
			if(tmp==null)break;
			mre = ByteUtil.union(mre, tmp);
				}catch (Exception e){
					break;
				}
			
			
			}while(true);
			io.close();
			return mre;
		}
		return null;
	}

	@Override
	public void setTimeout(int timeout) {
		// TODO Auto-generated method stub
		
	}

}
