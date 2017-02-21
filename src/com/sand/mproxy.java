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



public class mproxy implements Worker,Runnable{
	public static LogUtil mylog;
    Selector slt;
    static String URL;
    static String DoInfo;
    SelectionKey key = null;
    String hstr = "485454502F312E3120323030204F4B0D0A446174653A205765642C2031372053657020323031342031323A34313A323320474D540D0A566172793A204163636570742D456E636F64696E670D0A436F6E74656E742D456E636F64696E673A20677A69700D0A4B6565702D416C6976653A2074696D656F75743D31302C206D61783D31303030300D0A436F6E6E656374696F6E3A204B6565702D416C6976650D0A5472616E736665722D456E636F64696E673A206368756E6B65640D0A436F6E74656E742D547970653A20746578742F68746D6C3B636861727365743D5554462D380D0A436F6E74656E742D4C616E67756167653A20656E2D55530D0A0D0A36360D0A1F8B0800000000000003AA562A4B2D2ACECCCFF34BCC4D55B25232D433D03353D2514AC92FCFCBC94F4C292DCA018A6694941458E9EBE7E49617EB25E71597E665E6A5EB25E7E78245604AC19CE49CCCD4BC12BDC4826CA02150A381069829D502000000FFFF0D0A610D0A03001EFE91CE6A0000000D0A300D0A0D0A";
	
 // 压缩   
 	 public static byte[] compress(String str) throws IOException {   
 	    if (str == null || str.length() == 0) {   
 	     return null;   
 	   }   
 	    ByteArrayOutputStream out = new ByteArrayOutputStream();   
 	   GZIPOutputStream gzip = new GZIPOutputStream(out);   
 	    gzip.write(str.getBytes());   
 	    gzip.close();   
 	   return out.toByteArray();   
 	  }   
 	
 	
 	// 解压缩   
 	 public static String uncompress(String str) throws IOException {   
 	    if (str == null || str.length() == 0) {   
 	      return str;   
 	  }   
 	   ByteArrayOutputStream out = new ByteArrayOutputStream();   
 	   ByteArrayInputStream in = new ByteArrayInputStream(str   
 	        .getBytes("ISO-8859-1"));   
 	    GZIPInputStream gunzip = new GZIPInputStream(in);   
 	    byte[] buffer = new byte[256];   
 	    int n;   
 	   while ((n = gunzip.read(buffer))>= 0) {   
 	    out.write(buffer, 0, n);   
 	    }   
 	    // toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)   
 	    return out.toString();   
 	  }   
    
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
	    
	    buff.limit(buff.capacity());
	    buff.position(0);
	    int ilen = 0;
		try {
			ilen = sc.read(buff);
		    buff.flip();
		    byte[] getdata = buff.array();
		    byte[] getbyte =  ByteUtil.sub(getdata, 0, ilen);
		  
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
		mylog.i(new String(getdata));
		boolean cHost = false;
		/*分析数据 发送后台*/
		int of = 0,i,len = 0,endd=0;
		for(i=0;i<getdata.length;i++){
			if(getdata[i]==0x0d&&getdata[i+2]==0x0d){/*找到内容头*/
				of = i+4;
				len = getdata.length - of;
				break;
			}
		}
		byte[] heads = ByteUtil.sub(getdata, 0, i);
		String head = new String(heads);
		String[] params = head.split("\r\n");
		for(int l=0;l<params.length;l++){
			if(params[l].contains("Host")){
				params[l] = "Host: "+URL;/*替换头*/
				cHost = true;
			}else if(params[l].contains("gzip")){
				params[l] = "";/*替换头*/
			}
		}
		head = "";
		for(int l=0;l<params.length;l++){
			if( params[l].length()>0)head = head + params[l]+"\r\n";
			System.out.println(params[l]);
		}
		head = head + "\r\n";
		if(cHost){
		if(len>0)getdata = ByteUtil.union(head.getBytes(), ByteUtil.sub(getdata, of, len));
		else getdata = head.getBytes();
		mylog.i("EX SEND",getdata);
		}
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
			boolean dealex = false;
			boolean end_head = false;
			System.out.println("mre.len="+mre.length);
			for(i=0;i<mre.length;i++){
				if(mre[i]==0x0d&&mre[i+1]==0x0a&&mre[i+2]==0x0d){/*找到内容头*/
					of = i+4;
					i+=4;
					end_head = true;
					System.out.println("deal:"+of);
				}
				if(end_head){
					if(mre[i]==0x0d&&mre[i+1]==0x0a){
						of = i+2;
						i+=2;
						System.out.println("head:"+of);
						end_head = false;
						len = mre.length-of-7-8-11;
						break;
					}
				}
				if(mre[i]==0x00&&mre[i+1]==0x00&&mre[i+2]==0xff&&mre[i+3]==0xff){
					System.out.println("end:"+of);
					len = i - of;
					endd = i+11;
					break;
				}
			}
			heads = ByteUtil.sub(mre, 0, i);
			
			head = new String(heads);
			params = head.split("\r\n");
			for(int l=0;l<params.length;l++){
				if(params[l].contains("gzip")){
					params[l] = "";/*替换头*/
					dealex = true;
				}
			}
			head = "";
			for(int l=0;l<params.length;l++){
				if(params[l].length()>0&&params[l].contains(":")||params[l].contains("HTTP")){
					head = head + params[l]+"\r\n";
					System.out.println(params[l]);
				}
			}
			head= "HTTP/1.1 200 OK\r\nConnection: close\r\nContent-Type: text/html; charset=iso-8859-1\r\n";
			System.out.println(of+"-"+len);
			if(dealex){
			byte[] b_zip =ByteUtil.union(ByteUtil.sub(mre, of, len),ByteUtil.sub(mre, mre.length-15, 8));
			if(b_zip[9]==0x03)b_zip[10]=(byte) (b_zip[10]+1);
			
			System.out.println(ByteUtil.bytes2hex(b_zip));

			String re_txt = null;
			try {
				re_txt = uncompress(new String(b_zip,"ISO-8859-1"));
				int il = re_txt.length();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			head = head + "Content-Length: "+re_txt.length()+"\r\n\r\n";
			String resx = head+re_txt;
			System.out.println(resx);
			mylog.i("DealUnPress="+resx);
			return resx.getBytes();
			}else
			
			return mre;
		}
		return null;
	}


	@Override
	public void setTimeout(int timeout) {
		// TODO Auto-generated method stub
		
	}

}
