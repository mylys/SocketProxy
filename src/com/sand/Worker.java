package com.sand;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;


public interface Worker {
	public void init(LogUtil log, Selector s, String u, String info,SelectionKey k);
	public byte[] doClient(byte[] getdata) throws IOException;
	public void setTimeout(int timeout);
}
