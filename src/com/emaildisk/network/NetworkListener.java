package com.emaildisk.network;

public interface NetworkListener {
	public void bytesSent(int bytes);
	//public void uploadFinish(int totalBytes);
	public void bytesRead(int bytes);
	//public void downloadFinish(int totalBytes);
	
}
