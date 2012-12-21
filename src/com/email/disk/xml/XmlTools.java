package com.email.disk.xml;
import java.io.File;   


import java.io.FileWriter;   
import java.io.IOException;   
import java.io.Writer;   

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;   
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import com.emaildisk.network.ContentBean;
import com.emaildisk.network.DirectoryNodeBean;
import com.emaildisk.network.FileNodeBean;

import org.dom4j.Document;   
import org.dom4j.DocumentException;   
import org.dom4j.DocumentHelper;   
import org.dom4j.Element;   
import org.dom4j.io.SAXReader;   
import org.dom4j.io.XMLWriter;   

import com.emaildisk.conf.Config;
import com.emaildisk.network.NetworkIMAPHandler;

public class XmlTools {

	static Logger logger = Logger.getLogger(XmlTools.class.getName());
	public static boolean createContentMetasEmptyXml(String fileName) {
		Document document = DocumentHelper.createDocument();   
		Element content=document.addElement("ContentMetas");   
		Element version=content.addElement("Version");
		version.setText(Config.VERSION);
		Element blocks=content.addElement("Blocks");
		try 
		{   
			Writer fileWriter=new FileWriter(fileName);   
			XMLWriter xmlWriter=new XMLWriter(fileWriter);   
			xmlWriter.write(document);   
			xmlWriter.close();
			return true;
		} 
		catch (Exception e) 
		{   
			logger.warning(e.getMessage());
			return false;
		}   

		
	}
	public static boolean createNodeMetasEmptyXml(String fileName) {
		Document document = DocumentHelper.createDocument();   
		Element content=document.addElement("DirectoryNodeMetas");   
		Element version=content.addElement("Version");
		version.setText(Config.VERSION);
		Element updateDate=content.addElement("UpdateDate");
		updateDate.setText(new Date().toString());
		Element parent=content.addElement("Parent");
		parent.setText(".");
		Element nodes=content.addElement("Nodes");
		try 
		{   
			Writer fileWriter=new FileWriter(fileName);   
			XMLWriter xmlWriter=new XMLWriter(fileWriter);
			
			xmlWriter.write(document);   
			xmlWriter.close();
			return true;
		} 
		catch (Exception e) 
		{   
			logger.warning(e.getMessage());
			return false;
		}   
		
	}
	public static List<ContentBean> parseContentMetas(File inputXml)
	{
		SAXReader saxReader = new SAXReader();   
		try 
		{   
			Document document = saxReader.read(inputXml);   
			Element contentMetas=document.getRootElement();   
			for(Iterator i = contentMetas.elementIterator(); i.hasNext();)
			{   
				Element temp = (Element) i.next(); 
				String nodeName=temp.getName();
				if(nodeName.equals("Version"))
				{
					if(temp.getText().equals(Config.VERSION))
					{
						logger.info("Pass Version Check for Content Metas");
					}
					else
					{
						logger.warning("Different Version of Content Metas");
						return null;
					}
				}
				else if(nodeName.equals("Blocks"))
				{
					List<ContentBean> blockArray=new ArrayList<ContentBean>();
					for(Iterator blockIt = temp.elementIterator(); blockIt.hasNext();)
					{
						Element blockTmp=(Element)blockIt.next();
						String blockNodeName=blockTmp.getName();
						if(blockTmp.getName().equals("Block"))
						{
							ContentBean bean=new ContentBean();
							for(Iterator blockDetailIt=blockTmp.elementIterator();blockDetailIt.hasNext();)
							{
								Element detailElement=(Element)blockDetailIt.next();
								String detailElementName=detailElement.getName();
								if(detailElementName.equals("MessageID"))
								{
									bean.setMessageID(detailElement.getText());
								}
								/*else if(detailElementName.equals("FileName"))
								{
									bean.setFileName(detailElement.getText());
								}*/
								else if(detailElementName.equals("Counter"))
								{
									try{
										bean.setCounter(Integer.valueOf(detailElement.getText()));
									}
									catch(Exception e)
									{
										logger.warning("Parse Counter From Content Metas Failed");
										bean.setCounter(1);
									}
								}
								else if(detailElementName.equals("HashCode"))
								{
									bean.setHashCode(detailElement.getText());
								}
								else if(detailElementName.equals("UpdateDate"))
								{
									try
									{
									SimpleDateFormat format=new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",Locale.ENGLISH);
									Date updateDate=format.parse(detailElement.getText());
									bean.setUpdateDate(updateDate);
									}
									catch(Exception e)
									{
										logger.warning("Bad Update Date code-13");
										bean.setUpdateDate(new Date());
									}
								}
								else
								{
									logger.warning("Unexpected detail of block");
								}
							}
							blockArray.add(bean);
						}
						else
						{
							logger.warning("Unexpected XML File!");
						}
					}
					return blockArray;
				}
			}
			return null;
		} 
		catch (Exception e) {   
			logger.warning(e.getMessage());
			return null;
		}   
		   
		   
	}
	public static File makeUpContentMetas(List<ContentBean> blockArray)
	{
		Document document = DocumentHelper.createDocument();   
		Element content=document.addElement("ContentMetas");   
		Element version=content.addElement("Version");
		version.setText(Config.VERSION);
		Element blocks=content.addElement("Blocks");
		for(int i=0;i<blockArray.size();i++)
		{
			Element block=blocks.addElement("Block");
			ContentBean bean=blockArray.get(i);
			Element MessageID=block.addElement("MessageID");
			MessageID.setText(bean.getMessageID());
			Element HashCode=block.addElement("HashCode");
			HashCode.setText(bean.getHashCode());
			/*Element FileName=block.addElement("FileName");
			FileName.setText(bean.getFileName());*/
			Element UpdateDate=block.addElement("UpdateDate");
			UpdateDate.setText(bean.getUpdateDate().toString());
			Element Counter=block.addElement("Counter");
			Counter.setText(String.valueOf(bean.getCounter()));
		}
		try 
		{   
			File file=File.createTempFile("EMAILDISK", "CREATETEMPFILE");
			file.deleteOnExit();
			Writer fileWriter=new FileWriter(file);   
			XMLWriter xmlWriter=new XMLWriter(fileWriter);   
			xmlWriter.write(document);   
			xmlWriter.close();
			return file;
		} 
		catch (Exception e) 
		{   
			logger.warning(e.getMessage());
			return null;
		}   
	}
	public static DirectoryNodeBean parseDirectoryNodeMeta(File inputXml)
	{
		SAXReader saxReader = new SAXReader();   
		try 
		{   
			Document document = saxReader.read(inputXml);   
			Element NodeMetas=document.getRootElement();
			DirectoryNodeBean bean=new DirectoryNodeBean();
			for(Iterator i = NodeMetas.elementIterator(); i.hasNext();)
			{
				Element temp = (Element) i.next(); 
				String nodeName=temp.getName();
				
				if(nodeName.equals("Version"))
				{
					if(temp.getText().equals(Config.VERSION))
					{
						logger.info("Pass Version Check for Dir Metas");
					}
					else
					{
						logger.warning("Different Version of Dir Metas");
						return null;
					}
				}
				else if(nodeName.equals("Files"))
				{
					Map<String,String> fileArray=new HashMap<String,String>();
					for(Iterator fileIt=temp.elementIterator();fileIt.hasNext();)
					{
						Element filedetail=(Element)fileIt.next();
						if(filedetail.getName().equals("File"))
						{
							String singleName=null;
							String singleHash=null;
							for(Iterator fileDetailIt=filedetail.elementIterator();fileDetailIt.hasNext();)
							{
								Element fileDetailTempNode=(Element)fileDetailIt.next();
								String fileDetailTempName=fileDetailTempNode.getName();
								if(fileDetailTempName.equals("FileName"))
								{
									singleName=fileDetailTempNode.getText();
								}
								else if(fileDetailTempName.equals("HashCode"))
								{
									singleHash=fileDetailTempNode.getText();
								}
							}
							fileArray.put(singleName, singleHash);
						}
						else
						{
							logger.warning("Unexpected directory metas in file section!");
						}
					}
					bean.setFileNodeCollection(fileArray);
				}
				else if(nodeName.equals("Directorys"))
				{
					Map<String,String> dirArray=new HashMap<String,String>();
					for(Iterator dirIt=temp.elementIterator();dirIt.hasNext();)
					{
						Element directorydetail=(Element)dirIt.next();
						if(directorydetail.getName().equals("Directory"))
						{
							String singleName=null;
							String singleHash=null;
							for(Iterator directorydetailIt=directorydetail.elementIterator();directorydetailIt.hasNext();)
							{
								Element directoryDetailTempNode=(Element)directorydetailIt.next();
								String directoryDetailTempName=directoryDetailTempNode.getName();
								if(directoryDetailTempName.equals("DirectoryName"))
								{
									singleName=directoryDetailTempNode.getText();
								}
								else if(directoryDetailTempName.equals("HashCode"))
								{
									singleHash=directoryDetailTempNode.getText();
								}
							}
							dirArray.put(singleName, singleHash);
						}
						else
						{
							logger.warning("Unexpected directory metas in directory section!");
						}
					}
					bean.setDirectoryNodeCollection(dirArray);
				}
				else if(nodeName.equals("Parent"))
				{
					bean.setParent(temp.getText());
				}
				else if(nodeName.equals("UpdateDate"))
				{
					try
					{
					SimpleDateFormat format=new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",Locale.ENGLISH);
					Date updateDate=format.parse(temp.getText());
					bean.setUpdateDate(updateDate);
					
					}
					catch(Exception e)
					{
						//e.printStackTrace();
						logger.warning("Bad Update Date code-14");
						bean.setUpdateDate(new Date());
					}
				}
			}
			return bean;
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
			return null;
		}
	}
	public static FileNodeBean parseFileNodeMeta(File inputXml)
	{
		SAXReader saxReader = new SAXReader();   
		try 
		{   
			Document document = saxReader.read(inputXml);   
			Element FileNodeMetas=document.getRootElement();
			FileNodeBean bean=new FileNodeBean();
			for(Iterator i = FileNodeMetas.elementIterator(); i.hasNext();)
			{
				Element temp = (Element) i.next(); 
				String nodeName=temp.getName();
				if(nodeName.equals("Version"))
				{
					if(temp.getText().equals(Config.VERSION))
					{
						logger.info("Pass Version Check for FileNode Metas");
					}
					else
					{
						logger.warning("Different Version of FileNode Metas");
						return null;
					}
				}
				else if(nodeName.equals("Blocks"))
				{
					for(Iterator blockit=temp.elementIterator();blockit.hasNext();)
					{
						Element blocktemp=(Element)blockit.next();
						if(blocktemp.getName().equals("Block"))
						{
							bean.addBlockHash(blocktemp.getName());
						}
						else
						{
							logger.warning("Unexpected block section in filenodemetas");
						}
					}
				}
				else if(nodeName.equals("Parent"))
				{
					bean.setParent(temp.getText());
				}
				else if(nodeName.equals("FileName"))
				{
					bean.setFileName(temp.getText());
				}
				else if(nodeName.equals("UpdateDate"))
				{
					try
					{
					SimpleDateFormat format=new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",Locale.ENGLISH);
					Date updateDate=format.parse(temp.getText());
					bean.setUpdateDate(updateDate);
					}
					catch(Exception e)
					{
						
						logger.warning("Bad Update Date:code-12");
						bean.setUpdateDate(new Date());
					}
				}
			}
			return bean;
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
			return null;
		}
	}
	public static File makeUpDirectoryNodeMetas(DirectoryNodeBean bean)
	{
		Document document = DocumentHelper.createDocument();   
		Element content=document.addElement("DirectoryNodeMetas");   
		Element version=content.addElement("Version");
		version.setText(Config.VERSION);
		Element updateDate=content.addElement("UpdateDate");
		updateDate.setText(bean.getUpdateDate().toString());
		Element Directorys=content.addElement("Directorys");
		Map<String,String> dirArray=bean.getDirectoryNodeCollection();
		Iterator iter = dirArray.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			String SingleName = (String)entry.getKey();
			String SingleHash = (String)entry.getValue();
			Element directoryTmp=Directorys.addElement("Directory");
			Element directoryNameTmp=directoryTmp.addElement("DirectoryName");
			directoryNameTmp.setText(SingleName);
			Element directoryHashTmp=directoryTmp.addElement("HashCode");
			directoryHashTmp.setText(SingleHash);
		}
		Element Files=content.addElement("Files");
		Map<String,String> fileArray=bean.getFileNodeCollection();
		Iterator fileIter = fileArray.entrySet().iterator();
		while (fileIter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) fileIter.next();
			String SingleName = (String)entry.getKey();
			String SingleHash = (String)entry.getValue();
			Element fileTmp=Files.addElement("File");
			Element fileNameTmp=fileTmp.addElement("FileName");
			fileNameTmp.setText(SingleName);
			Element fileHashTmp=fileTmp.addElement("HashCode");
			fileHashTmp.setText(SingleHash);
		}
		try 
		{   
			File file=File.createTempFile("EMAILDISK", "CREATETEMPFILE");
			file.deleteOnExit();
			if(Config.debugFlag)
			{
				logger.warning("Node Metas Making Up Test");
				logger.warning(file.getAbsolutePath());
			}
			Writer fileWriter=new FileWriter(file);   
			XMLWriter xmlWriter=new XMLWriter(fileWriter);   
			xmlWriter.write(document);   
			xmlWriter.close();
			return file;
		} 
		catch (Exception e) 
		{   
			logger.warning(e.getMessage());
			return null;
		}   
	}
	public static File makeUpFileNodeMetas(FileNodeBean bean)
	{
		Document document = DocumentHelper.createDocument();   
		Element content=document.addElement("FileNodeMetas");   
		Element version=content.addElement("Version");
		version.setText(Config.VERSION);
		Element updateDate=content.addElement("UpdateDate");
		updateDate.setText(bean.getUpdateDate().toString());
		Element fileName=content.addElement("FileName");
		fileName.setText(bean.getFileName());
		Element blocks=content.addElement("Blocks");
		List<String> blockArray=bean.getHashCollection();
		for(int i=0;i<blockArray.size();i++)
		{
			Element block=blocks.addElement("Block");
			block.setText(blockArray.get(i));
		}
		try 
		{   
			File file=File.createTempFile("EMAILDISK", "CREATETEMPFILE");
			file.deleteOnExit();
			if(Config.debugFlag)
			{
				logger.warning("Note File Metas Making Up Test");
				logger.warning(file.getAbsolutePath());
			}
			Writer fileWriter=new FileWriter(file);   
			XMLWriter xmlWriter=new XMLWriter(fileWriter);   
			xmlWriter.write(document);   
			xmlWriter.close();
			return file;
		} 
		catch (Exception e) 
		{   
			logger.warning(e.getMessage());
			return null;
		}   
	}
	

}
