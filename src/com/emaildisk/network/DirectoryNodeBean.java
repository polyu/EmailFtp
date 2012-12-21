package com.emaildisk.network;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectoryNodeBean {
	private String parent="";
	private Date updateDate=new Date();
	private String directoryName="";
	private Map<String,String> directoryNodeCollection=new HashMap<String,String>();
	
	private Map<String,String> fileNodeCollection=new HashMap<String,String>();
	
	public void setUpdateDate(Date date)
	{
		this.updateDate=date;
	}
	public Date getUpdateDate()
	{
		return this.updateDate;
	}
	public void setParent(String parent)
	{
		this.parent=parent;
	}
	public String getParent()
	{
		return this.parent;
	}
	public void setDirectoryName(String DirectoryName)
	{
		this.directoryName=DirectoryName;
	}
	public String getDirectoryName()
	{
		return this.getDirectoryName();
	}
	public Map<String,String> getDirectoryNodeCollection()
	{
		return this.directoryNodeCollection;
	}
	public Map<String,String> getFileNodeCollection()
	{
		return this.fileNodeCollection;
	}
	public void setFileNodeCollection(Map<String,String> fileNodeCollection)
	{
		this.fileNodeCollection=fileNodeCollection;
	}
	public void setDirectoryNodeCollection(Map<String,String> directoryNodeCollection)
	{
		this.directoryNodeCollection=directoryNodeCollection;
	}
	
}
