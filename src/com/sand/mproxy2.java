package com.sand;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


//implements多重接口实现
public class mproxy2 implements Worker,Runnable{
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
	    SocketChannel socketChannel = (SocketChannel)key.channel();
	    
	    buff.limit(buff.capacity());
	    buff.position(0);
	    int ilen = 0;
		try {
			 
			ilen = socketChannel.read(buff);
		    buff.flip();
		    byte[] getdata = buff.array();
		    byte[] getbyte =  ByteUtil.sub(getdata, 0, ilen);
		  
		    mylog.i("["+Thread.currentThread().toString()+"]read=",getbyte);
	        byte[] resend = doClient(getbyte);
	        int total = resend.length;
	        int sublen = 0;
	        mylog.i("["+Thread.currentThread().toString()+"]["+total+"]send=",resend);
	        for(int i=0;i<=total/1000;i++){
	        if(total-i*1000<0)sublen = total;
	        else sublen = 1000;
	        ByteBuffer mmbb = ByteBuffer.wrap(ByteUtil.sub(resend, i*1000, sublen));
	        if(resend.length<2000)mylog.i(new String(resend));
	        ilen = socketChannel.write(mmbb);
	        mylog.i( "send"+ilen);
	        }
	       
		}catch (Exception e) {
			mylog.i("["+Thread.currentThread().toString()+"]"+e.getMessage());
			mylog.i("["+Thread.currentThread().toString()+"]"+e.getLocalizedMessage());
			e.printStackTrace();
		}finally{
			try {
				if(socketChannel!=null)socketChannel.close();
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
