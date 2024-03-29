package com.emaildisk.mainloop;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.email.disk.xml.XmlTools;
import com.emaildisk.io.FileSplitter;
import com.emaildisk.io.GZipUtils;
import com.emaildisk.io.HashCalc;
import com.emaildisk.io.ZlibUtils;
import com.emaildisk.network.DirectoryNodeBean;
import com.emaildisk.network.FileNodeBean;
import com.emaildisk.network.NetworkIMAPHandler;

public class Debug {
	
	public static void main(String []args)
	{
		DirectoryNodeBean bean=new DirectoryNodeBean();
		bean.setDirectoryName("first-dir");
		bean.setParent("ROOT");
		Map<String,String> map=bean.getDirectoryNodeCollection();
		map.put("subdir1", "randomhash1");
		map.put("subdir2", "randomhash2");
		map.put("subdir3", "randomhash3");
		map=bean.getFileNodeCollection();
		map.put("subfile1", "randomhash1");
		map.put("subfile2", "randomhash2");
		map.put("subfile3", "randomhash3");
		XmlTools.makeUpDirectoryNodeMetas(bean);
		FileNodeBean fbean=new FileNodeBean();
		fbean.setFileName("fsd.xml");
		List<String> array=fbean.getHashCollection();
		array.add("fdsfdsf");
		array.add("fdsfdsf22131");
		array.add("fdsfdsfsdasdsadsad");
		XmlTools.makeUpFileNodeMetas(fbean);
		/*try
		{
			NetworkIMAPHandler handler=new NetworkIMAPHandler();
			if(handler.createSession("imap.163.com", "sysuhdzx", "276201510"))
			{
				System.out.println("Login!");
				if(handler.createWorkingDirectorys())
				{
					System.out.println("Created!");
				}
				else
				{
					System.out.println("Fail Creating!");
				}
				handler.getContentMetas();
				handler.getRootNodeMetas();
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
		}*/
		/*try
		{
		File f=new File("c:/test.txt");
		BufferedOutputStream os=new BufferedOutputStream(new FileOutputStream(f));
		//Byte s12[]={'<','h','s','a','h','>'};
		//Byte t12[]={'<','h','s','a','h','>','/'};
		for(int i=0;i<3200;i++)
		{	
			os.write("<hash>".getBytes());
			os.write(randomString(16).getBytes());
			os.write("</hash>".getBytes());
		}
		os.flush();
		os.close();
		GZipUtils.compress(f,false);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}*/
	}
	private static Random randGen = null;
	private static char[] numbersAndLetters = null;

	public static final String randomString(int length) {
	         if (length < 1) {
	             return null;
	         }
	         if (randGen == null) {
	                randGen = new Random();
	                numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
	                   "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
	                  //numbersAndLetters = ("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
	                 }
	         char [] randBuffer = new char[length];
	         for (int i=0; i<randBuffer.length; i++) {
	             randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
	          //randBuffer[i] = numbersAndLetters[randGen.nextInt(35)];
	         }
	         return new String(randBuffer);
	}
	
	
}
