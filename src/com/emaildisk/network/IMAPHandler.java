package com.emaildisk.network;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.SearchTerm;






import com.email.disk.xml.DomXmlHandler;
import com.emaildisk.conf.Config;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPStore;

public class IMAPHandler {
	static Logger logger = Logger.getLogger(IMAPHandler.class.getName());
	
	Properties prop = null;
	Session session=null;
	IMAPStore store = null;
	boolean initFlag=false;
	IMAPFolder nodeFolder=null;
	IMAPFolder contentFolder=null;
	File localContentMetasFile=null;
	File localNodeMetasFile=null;
	public boolean createSession(String IMAPHost,String user,String password)
	{
		 prop = new Properties();
		 prop.put("mail.store.protocol", "imap");  
		 prop.put("mail.imap.host", IMAPHost);
		 
		 try
		 {
			 session = Session.getInstance(prop); 
			 store = (IMAPStore) session.getStore("imap");
			 store.connect(user, password);  
			 initFlag=true;
			 return true;
		 }
		 catch(Exception e)
		 {
			 //e.printStackTrace();
			 logger.warning(e.getMessage());
			 return false;
		 }
	}
	
	public boolean initEmailDisk()
	{
		try
		{
			nodeFolder=(IMAPFolder)store.getFolder("node");
			contentFolder=(IMAPFolder)store.getFolder("content");
			if(!nodeFolder.exists())
			{
				nodeFolder.create(Folder.HOLDS_MESSAGES);
				nodeFolder.open(Folder.READ_WRITE);
				if(Config.debugFlag) logger.warning("Init node folder");
				boolean r=createEmptyNodeMeta();
				if(!r)
				{
					logger.warning("Init node folder failed!");
					return false;
				}
			}
			else
			{
				if(Config.debugFlag) logger.warning("Node Folder Existed!");
				nodeFolder.open(Folder.READ_WRITE);
			}
			if(!contentFolder.exists())
			{
				contentFolder.create(Folder.HOLDS_MESSAGES);
				contentFolder.open(Folder.READ_WRITE);
				if(Config.debugFlag) logger.warning("Init content folder");
				boolean r=createEmptyContentMeta();
				if(!r)
				{
					logger.warning("Init content folder failed!");
					return false;
				}
			}
			else
			{
				if(Config.debugFlag) logger.warning("Content Folder Existed!");
				contentFolder.open(Folder.READ_WRITE);
			}
			initFlag=true;
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	public boolean createContentNode()
	{
		return true;
	}
	public boolean createDirectoryNode()
	{
		return true;
	}
	public boolean createContent()
	{
		return true;
	}
	
	public boolean createEmptyNodeMeta()
	{
		if(nodeFolder==null) return false;
		
		try
		{
			
			Message msg=new MimeMessage(session);
			msg.setSubject("..NodeMetas..");
			Multipart multipart = new MimeMultipart();
			//=====Text=============
			MimeBodyPart messageBodyPart =  new MimeBodyPart();
			messageBodyPart.setText("Meta Data for Node");
			//======================
			//====Attach===========
			MimeBodyPart AttachmentBodyPart = new MimeBodyPart();
			 //===Create File=================
			 File tmpFile=File.createTempFile("nodemetaempty", "xml");
			 String fileAttachment=tmpFile.getAbsolutePath();
			 boolean r=DomXmlHandler.createNodeMetasEmptyXml(fileAttachment);
			 if(!r)
			 {
				 logger.warning("Create Empty Nodes Xml Failed!");
				 return false;
			 }
			 //====================
		     DataSource source = new FileDataSource(fileAttachment);
		     AttachmentBodyPart.setDataHandler(new DataHandler(source));
		     AttachmentBodyPart.setFileName("NodeMetas.xml");
			//=====================
		       multipart.addBodyPart(messageBodyPart);
		       multipart.addBodyPart(AttachmentBodyPart);
		     msg.setContent(multipart);
			Message []msgs={msg};
			nodeFolder.addMessages(msgs);
			return true;
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
			return false;
		}
	}
	public boolean createEmptyContentMeta()
	{
		if(contentFolder==null) return false;
		
		try
		{
			Message msg=new MimeMessage(session);
			msg.setSubject("..ContentMetas..");
			Multipart multipart = new MimeMultipart();
			//=====Text=============
			MimeBodyPart messageBodyPart =  new MimeBodyPart();
			messageBodyPart.setText("Meta Data for Content");
			//======================
			//====Attach===========
			MimeBodyPart AttachmentBodyPart = new MimeBodyPart();
			 //===Create File=================
			 File tmpFile=File.createTempFile("contentmetaempty", "xml");
			 String fileAttachment=tmpFile.getAbsolutePath();
			 boolean r=DomXmlHandler.createContentMetasEmptyXml(fileAttachment);
			 if(!r)
			 {
				 logger.warning("Create Empty Content Xml Failed!");
				 return false;
			 }
			 //====================
		     DataSource source = new FileDataSource(fileAttachment);
		     AttachmentBodyPart.setDataHandler(new DataHandler(source));
		     AttachmentBodyPart.setFileName("ContentMetas.xml");
			//=====================
		       multipart.addBodyPart(messageBodyPart);
		       multipart.addBodyPart(AttachmentBodyPart);
		     msg.setContent(multipart);
			Message []msgs={msg};
			contentFolder.addMessages(msgs);
			return true;
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
			return false;
		}
	}
	public void closeEmailDisk()
	{
		try
		{
			contentFolder.close(true);
		}
		catch(Exception e)
		{
		}
		try
		{
			nodeFolder.close(true);
		}
		catch(Exception e)
		{
		}
		try
		{
			store.close();
		}
		catch(Exception e)
		{
		}
		
	}
	public boolean getContentMetas()
	{
		if(!initFlag||contentFolder==null) return false;
		try
		{
			
			Message []metas=contentFolder.search(new IMAPSubjectSearchTerm("..ContentMetas.."));
			if(metas.length<=0)
			{
				logger.warning("Bad Content Metas");
				return false;
			}
			else if(metas.length>1)
			{
				logger.warning("Haven't implement Content meta");
				return false;
			}
			else
			{
				
				return true;
			}
			
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
			return false;
		}
		
	}
	public boolean getNodeMetas()
	{
		if(!initFlag||nodeFolder==null) return false;
		try
		{
			Message []metas=nodeFolder.search(new IMAPSubjectSearchTerm("..NodeMetas.."));
			if(metas.length<=0)
			{
				logger.warning("Bad Node Metas");
				return false;
			}
			else if(metas.length>1)
			{
				logger.warning("Haven't implement node meta");
				return false;
			}
			else
			{
				return true;
			}
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
			return false;
		}
	}
}
