package com.emaildisk.network;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DirectoryNodeBean {
	private String parent="";
	private Date updateDate=new Date();
	private String directoryName="";
	private List<String> directoryNodeCollection=new ArrayList<String>();
	private List<String> directoryNameCollection=new ArrayList<String>();
	private List<String> fileNodeCollection=new ArrayList<String>();
	private List<String> fileNameCollection=new ArrayList<String>();
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
	public List<String> getDirectoryNodeCollection()
	{
		return this.directoryNodeCollection;
	}
	public List<String> getFileNodeCollection()
	{
		return this.fileNodeCollection;
	}
	public void setFileNodeCollection(List<String> fileNodeCollection)
	{
		this.fileNodeCollection=fileNodeCollection;
	}
	public void setDirectoryNodeCollection(List<String> directoryNodeCollection)
	{
		this.directoryNodeCollection=directoryNodeCollection;
	}
	public List<String> getDirectoryNameCollection()
	{
		return this.directoryNameCollection;
	}
	public List<String> getFileNameCollection()
	{
		return this.fileNameCollection;
	}
	public void setFileNameCollection(List<String> fileNameCollection)
	{
		this.fileNameCollection=fileNameCollection;
	}
	public void setDirectoryNameCollection(List<String> directoryNameCollection)
	{
		this.directoryNameCollection=directoryNameCollection;
	}
}
