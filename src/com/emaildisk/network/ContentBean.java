package com.emaildisk.network;

import java.util.Date;

public class ContentBean {
	private String fileName="";
	private String hashCode="";
	private int counter=0;
	private String messageID="";
	private Date updateDate=new Date();
	public void setFileName(String fileName)
	{
		this.fileName=fileName;
	}
	public void setHashCode(String hashCode)
	{
		this.hashCode=hashCode;
	}
	public void setCounter(int Counter)
	{
		this.counter=Counter;
	}
	public void setMessageID(String MessageID)
	{
		this.messageID=MessageID;
	}
	public void setUpdateDate(Date date)
	{
		this.updateDate=date;
	}
	public Date getUpdateDate()
	{
		return this.updateDate;
	}
	public int getCounter()
	{
		return this.counter;
	}
	public String getMessageID()
	{
		return this.messageID;
	}
	public String getFileName()
	{
		return this.fileName;
	}
	public String getHashCode()
	{
		return this.hashCode;
	}
}
