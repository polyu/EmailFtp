package com.emaildisk.network;

import java.io.BufferedWriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.SearchTerm;






import com.email.disk.xml.XmlTools;
import com.emaildisk.conf.Config;
import com.emaildisk.io.CountingFileDataSource;
import com.emaildisk.io.GZipUtils;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPStore;

public class NetworkIMAPHandler {
	static Logger logger = Logger.getLogger(NetworkIMAPHandler.class.getName());
	
	Properties prop = null;
	Session session=null;
	IMAPStore store = null;
	boolean loginFlag=false;
	IMAPFolder nodeFolder=null;
	IMAPFolder sessionFolder=null;
	IMAPFolder contentFolder=null;
	List<ContentBean> contentBlockArray=null;
	DirectoryNodeBean currentDirectoryNodeBean=null;
	private NetworkListener nl=null;
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
			 loginFlag=true;
			 return true;
		 }
		 catch(Exception e)
		 {
			 //e.printStackTrace();
			 logger.warning(e.getMessage());
			 return false;
		 }
	}
	
	public boolean createWorkingDirectorys()
	{
		try
		{
			nodeFolder=(IMAPFolder)store.getFolder("node");
			contentFolder=(IMAPFolder)store.getFolder("content");
			sessionFolder=(IMAPFolder)store.getFolder("session");
			if(!nodeFolder.exists())
			{
				nodeFolder.create(Folder.HOLDS_MESSAGES);
				nodeFolder.open(Folder.READ_WRITE);
				logger.info("Init node folder");
				boolean r=createEmptyNodeMeta();
				if(!r)
				{
					logger.warning("Init node folder failed!");
					return false;
				}
			}
			else
			{
				logger.info("Node Folder Existed!");
				nodeFolder.open(Folder.READ_WRITE);
			}
			if(!contentFolder.exists())
			{
				contentFolder.create(Folder.HOLDS_MESSAGES);
				contentFolder.open(Folder.READ_WRITE);
				logger.info("Init content folder");
				boolean r=createEmptyContentMeta();
				if(!r)
				{
					logger.warning("Init content folder failed!");
					return false;
				}
			}
			else
			{
				logger.info("Content Folder Existed!");
				contentFolder.open(Folder.READ_WRITE);
			}
			if(!sessionFolder.exists())
			{
				sessionFolder.create(Folder.HOLDS_MESSAGES);
				sessionFolder.open(Folder.READ_WRITE);
				logger.info("Create Session folder");
			}
			else
			{
				logger.info("Session Folder Existed!");
				sessionFolder.open(Folder.READ_WRITE);
			}
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
		File tmpFile=null;
		File gzipTmpFile=null;
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
			 tmpFile=File.createTempFile("nodemetaempty", "xml");
			 gzipTmpFile=File.createTempFile("nodemetaempty", "xml.gz");
			 String fileAttachment=tmpFile.getAbsolutePath();
			 boolean r=XmlTools.createNodeMetasEmptyXml(fileAttachment);
			 if(!r)
			 {
				 logger.warning("Create Empty Nodes Xml Failed!");
				 return false;
			 }
			 
			 //====================
			 if(Config.COMPRESSCONFFILE)
			 {
				 GZipUtils.compress(tmpFile,gzipTmpFile, false);
				 DataSource source = new CountingFileDataSource(gzipTmpFile.getAbsoluteFile(),nl);
				 AttachmentBodyPart.setDataHandler(new DataHandler(source));
			 }
			 else
			 {
				 DataSource source = new CountingFileDataSource(tmpFile.getAbsoluteFile(),nl);
			     AttachmentBodyPart.setDataHandler(new DataHandler(source));
			 }
		     AttachmentBodyPart.setFileName("NodeMetas.xml");
			//=====================
		       multipart.addBodyPart(messageBodyPart);
		       multipart.addBodyPart(AttachmentBodyPart);
		     msg.setContent(multipart);
			Message []msgs={msg};
			int currentMessageCount=nodeFolder.getMessageCount();
			nodeFolder.appendMessages(msgs);
			while(currentMessageCount==nodeFolder.getMessageCount())
		       {
		    	   logger.info("Waiting for sending up!");
		    	   Thread.sleep(Config.SENDWAITTIME);
		       }
			//Message returnMsgs[]=nodeFolder.addMessages(msgs);
			//logger.info("Node Meta Message Id:"+((IMAPMessage)returnMsgs[0]).getMessageID());
			return true;
		}
		catch(Exception e)
		{
			
			logger.warning(e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if(tmpFile!=null) tmpFile.delete();
				if(gzipTmpFile!=null) gzipTmpFile.delete();
			}
			catch(Exception e)
			{
				
			}
		}
	}
	public boolean createEmptyContentMeta()
	{
		if(contentFolder==null) return false;
		File tmpFile=null;
		File gzipTmpFile=null;
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
			 tmpFile=File.createTempFile("nodemetaempty", "xml");
			 gzipTmpFile=File.createTempFile("nodemetaempty", "xml.gz");
			 
			 String fileAttachment=tmpFile.getAbsolutePath();
			 boolean r=XmlTools.createContentMetasEmptyXml(fileAttachment);
			 if(!r)
			 {
				 logger.warning("Create Empty Content Xml Failed!");
				 return false;
			 }
			 
			 //====================
			 if(Config.COMPRESSCONFFILE)
			 {
				 GZipUtils.compress(tmpFile,gzipTmpFile, false);
				 DataSource source = new CountingFileDataSource(gzipTmpFile.getAbsoluteFile(),nl);
				 AttachmentBodyPart.setDataHandler(new DataHandler(source));
			 }
			 else
			 {
				 DataSource source = new CountingFileDataSource(tmpFile.getAbsoluteFile(),nl);
			     AttachmentBodyPart.setDataHandler(new DataHandler(source));
			 }
		     AttachmentBodyPart.setFileName("ContentMetas.xml");
			//=====================
		       multipart.addBodyPart(messageBodyPart);
		       multipart.addBodyPart(AttachmentBodyPart);
		       msg.setContent(multipart);
		       Message []msgs={msg};
		       int currentMessageCount=contentFolder.getMessageCount();
		       contentFolder.appendMessages(msgs);
		       //Message []returnMsgs=contentFolder.addMessages(msgs);
		       //System.out.println(returnMsgs[0].getMessageNumber());
		       //logger.info("Content Meta Message Id:"+((IMAPMessage)returnMsgs[0]).getMessageID());
		       while(currentMessageCount==contentFolder.getMessageCount())
		       {
		    	   logger.info("Waiting for sending up!");
		    	   Thread.sleep(Config.SENDWAITTIME);
		       }
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.warning(e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if(tmpFile!=null) tmpFile.delete();
				if(gzipTmpFile!=null) gzipTmpFile.delete();
			}
			catch(Exception e)
			{
				
			}
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
		if(!loginFlag||contentFolder==null) return false;
		try
		{
			
			Message []metas=contentFolder.search(new NetworkIMAPSubjectSearchTerm("..ContentMetas.."));
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
				File f=this.getAttachmentAsFile(metas[0]);
				if(Config.COMPRESSCONFFILE)
				{
					File unzipFile=File.createTempFile("EmailTask", "XMLMETA");
					GZipUtils.decompress(f, unzipFile, false);
					contentBlockArray=XmlTools.parseContentMetas(unzipFile);
					unzipFile.deleteOnExit();
				}
				else
				{
					contentBlockArray=XmlTools.parseContentMetas(f);
				}
				if(contentBlockArray==null)
				{
					logger.warning("Parse Content Meta Failed");
					return false;
				}
				if(Config.debugFlag)
				{
					for(int i=0;i<contentBlockArray.size();i++)
					{
						logger.warning("Debug:Message for content block!");
						logger.warning(contentBlockArray.get(i).getHashCode());
					}
				}
				return true;
			}
			
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			logger.warning(e.getMessage());
			return false;
		}
		
	}
	public boolean getRootNodeMetas()
	{
		if(!loginFlag||nodeFolder==null) return false;
		try
		{
			Message []metas=nodeFolder.search(new NetworkIMAPSubjectSearchTerm("..NodeMetas.."));
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
				File f=this.getAttachmentAsFile(metas[0]);
				if(Config.COMPRESSCONFFILE)
				{
					File unzipFile=File.createTempFile("EmailTask", "XMLMETA");
					unzipFile.deleteOnExit();
					GZipUtils.decompress(f, unzipFile, false);
					this.currentDirectoryNodeBean=XmlTools.parseDirectoryNodeMeta(unzipFile);
				}
				else
				{
					this.currentDirectoryNodeBean=XmlTools.parseDirectoryNodeMeta(f);
				}
				
				if(Config.debugFlag)
				{
					/*List<String> dir=this.currentDirectoryNodeBean.getDirectoryNodeCollection();
					logger.warning("Debug Message for dir from dir");
					for(int j=0;j<dir.size();j++)
					{
						logger.warning(dir.get(j));
					}
					List<String> file_1=this.currentDirectoryNodeBean.getFileNodeCollection();
					logger.warning("Debug Message for file from dir");
					for(int j=0;j<file_1.size();j++)
					{
						logger.warning(file_1.get(j));
					}*/
				}
				return true;
			}
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
			return false;
		}
	}
	boolean tryRestoreSession()
	{
		if(!this.loginFlag||this.sessionFolder==null) return false;
		logger.warning("Inspect Session havent implement!");
		return true;
	}
	public File getAttachmentAsFile(Message message)
	{
		try
		{
			Multipart multipart = (Multipart) message.getContent();
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				if(!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
				  continue; // dealing with attachments only
				} 
				logger.info("Ready to Read from Mail:"+bodyPart.getSize());
				InputStream is = bodyPart.getInputStream();
				File f = File.createTempFile("emaildisk", "getattach");
				f.deleteOnExit();
				BufferedOutputStream fos= new BufferedOutputStream(new FileOutputStream(f));
				byte[] buf = new byte[4096];
				int bytesRead;
				while((bytesRead = is.read(buf))!=-1) {
				    fos.write(buf, 0, bytesRead);
				}
				fos.close();
				return f;
			}
			return null;
		}
		catch(Exception e)
		{
		logger.warning(e.getMessage());
		return null;
		}
	}
	public byte[] getAttachmentAsByte(Message message)
	{
		try
		{
			Multipart multipart = (Multipart) message.getContent();
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				if(!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
				  continue; // dealing with attachments only
				} 
				logger.info("Ready to Read from Mail:"+bodyPart.getSize());
				InputStream is = bodyPart.getInputStream();
				int chunk=bodyPart.getSize();
				byte[] buf = new byte[chunk];
				
				is.read(buf, 0, chunk);
				return buf;
			}
			return null;
		}
		catch(Exception e)
		{
		logger.warning(e.getMessage());
		return null;
		}
	}
	public boolean updateContentMetas()
	{
		if(!loginFlag||nodeFolder==null||sessionFolder==null||this.contentBlockArray==null) return false;
		return true;
	}
	
}
