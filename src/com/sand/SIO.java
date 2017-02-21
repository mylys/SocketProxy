package com.sand;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.CharBuffer;

public class SIO {
	private Socket client;
	private String IP = "";
	private int port = -1;
	private String lastError;
	private boolean isOk = false;
	
	private BufferedInputStream is;
	private DataOutputStream os;
	private byte[] recvbuf;
	private int bufsize = 1024*1024;
	public SIO(){
		recvbuf = new byte[bufsize];
		client = new Socket();
	};
	
	public SIO(String ip,int port){
		this.IP = ip;
		this.port = port;
		recvbuf = new byte[bufsize];
		try {
			client = new Socket(IP,port);
			client.setSoTimeout(1500);
			is =new BufferedInputStream(new DataInputStream(client.getInputStream()));
			os =new DataOutputStream(client.getOutputStream());
		} catch (UnknownHostException e) {
			isOk = false;
			lastError = e.getLocalizedMessage();
			e.printStackTrace();
			return;
		} catch (IOException e) {
			isOk = false;
			lastError = e.getLocalizedMessage();
			e.printStackTrace();
			return;
		}catch(Throwable a)
		{
			lastError = a.getMessage();
			isOk = false;
			return;
		}
		
			isOk = true;
	};
	
	public Socket getClient() {
		return client;
	}

	public void setClient(Socket client) {
		this.client = client;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getLastError() {
		return lastError;
	}

	public void setLastError(String lastError) {
		this.lastError = lastError;
	}

	public boolean isOk() {
		return isOk;
	}

	public void setOk(boolean isOk) {
		this.isOk = isOk;
	}

	public BufferedInputStream getIs() {
		return is;
	}

	public void setIs(BufferedInputStream is) {
		this.is = is;
	}

	public DataOutputStream getOs() {
		return os;
	}

	public void setOs(DataOutputStream os) {
		this.os = os;
	}

	public int getBufsize() {
		return bufsize;
	}

	public void setBufsize(int bufsize) {
		this.bufsize = bufsize;
		recvbuf = new byte[bufsize];
	}

	public void connect() throws Exception
	{
		if(client.isConnected())
		{
			
		}else{
			SocketAddress endpoint = new InetSocketAddress(IP,port);
			client.connect(endpoint,5000);
		}
	}
	
	public byte[] read() throws IOException{
		if(isOk)
		{
			int ilen = is.read(recvbuf);
			if(ilen<=0)return null;
			return ByteUtil.sub(recvbuf, 0, ilen);
		}
		return null;
	}
	
	public int write(byte[] data) throws IOException
	{
		os.write(data);
		os.flush();
		return 0;
	}
	
	public boolean close(){
		if(isOk)
		{
			try{
			os.flush();
			os.close();
			is.close();
			client.close();
			client = null;
			isOk = true;
			lastError = "has closed!";
			}catch(Exception e){
				lastError = e.getLocalizedMessage();
				return false;
			}
		}
		return false;
	}

	
}
