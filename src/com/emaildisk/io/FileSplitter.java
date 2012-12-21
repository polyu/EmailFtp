package com.emaildisk.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.logging.Logger;

import com.emaildisk.conf.Config;
import com.emaildisk.network.NetworkIMAPHandler;

public class FileSplitter {
	private RandomAccessFile srcFile=null;
	static Logger logger = Logger.getLogger(FileSplitter.class.getName());
	private String fileUrl;
	private String fileName;
	private String prefixFileName;
    private int totalBlocks=0;
    private long totalSize=0;
	public FileSplitter()
	{
		
	}
	public boolean initFileSplitter(String file)
	{
		
		try
		{
			this.fileUrl=file;
			File info=new File(file);
			this.fileName=info.getName();
			this.prefixFileName=cropFilename(this.fileName);
			srcFile=new RandomAccessFile(file,"r");
			totalSize=srcFile.length();
			
			if((totalSize%Config.BLOCKSIZE)!=0)
				totalBlocks=(int)(totalSize/Config.BLOCKSIZE)+1;
			else
				totalBlocks=(int)(totalSize/Config.BLOCKSIZE);
			logger.info("TotalSize:"+totalSize+" totalBlocks:"+totalBlocks);
			
			return true;
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
			srcFile=null;
			
			return false;
		}
		
	}
	
	private static String cropFilename(String fileName)
	{
		if(fileName==null) return fileName;
		String[] r=fileName.split("\\.");
		return r[0];
	}
	
	public byte[] getBlocksinMemory(int block)
	{
		try
		{
			if(srcFile!=null&&block>0&&block<=totalBlocks)
			{
				byte[] data=null;
				if(block!=totalBlocks)
				{
					data=new byte[(int)Config.BLOCKSIZE];
				}
				else
				{
					data=new byte[(int)(totalSize-(totalBlocks-1)*Config.BLOCKSIZE)];
				}
				logger.info("Current Offset:"+(block-1)*(int)Config.BLOCKSIZE);
				
				srcFile.seek((block-1)*(int)Config.BLOCKSIZE);
				srcFile.read(data);
				return data;
			}
			else
			{
				logger.warning("Not init Splitter or bad blocks");
				return null;
			}
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
			return null;
		}
	}
	public int getTotalBlocks()
	{
		return this.totalBlocks;
	}
	public long getTotalSize()
	{
		return this.totalSize;
	}
	public String getFileUrl()
	{
		return this.fileUrl;
	}
	public String getPrefixFileName()
	{
		return this.prefixFileName;
	}
	public void closeFileSplitter()
	{
		try
		{
			srcFile.close();
		}
		catch(Exception e)
		{
			
		}
	}
}
