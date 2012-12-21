package com.emaildisk.network;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileNodeBean {
	private String parent="";
	public String fileName="";
	private Date updateDate=new Date();
	private List<String> hashCollection=new ArrayList<String>();
	public void setParent(String parent)
	{
		this.parent=parent;
	}
	public String getFileName()
	{
		return this.fileName;
	}
	public void setFileName(String FileName)
	{
		this.fileName=FileName;
	}
	public void setUpdateDate(Date updateDate)
	{
		this.updateDate=updateDate;
	}
	public String getParent()
	{
		return this.parent;
	}
	public Date getUpdateDate()
	{
		return this.updateDate;
	}
	public List<String> getHashCollection()
	{
		return this.hashCollection;
	}
	public void setHashCollection(List<String> hashCollection)
	{
		this.hashCollection=hashCollection;
	}
	public void addBlockHash(String hashCode)
	{
		this.hashCollection.add(hashCode);
	}
}
