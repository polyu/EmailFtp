package com.emaildisk.mainloop;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import com.emaildisk.io.FileSplitter;
import com.emaildisk.io.GZipUtils;
import com.emaildisk.io.HashCalc;
import com.emaildisk.io.ZlibUtils;
import com.emaildisk.network.IMAPHandler;

public class Debug {
	
	public static void main(String []args)
	{
		try
		{
			IMAPHandler handler=new IMAPHandler();
			if(handler.createSession("imap.163.com", "sysuhdzx", "276201510"))
			{
				System.out.println("Login!");
				if(handler.initEmailDisk())
				{
					System.out.println("Created!");
				}
				else
				{
					System.out.println("Fail Creating!");
				}
				handler.getContentMetas();
				handler.closeEmailDisk();
			}
			else
			{
				System.out.println("Error Login!");
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	
	}
	
}
