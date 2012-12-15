package com.email.disk.xml;
import java.io.File;   
import java.io.FileWriter;   
import java.io.IOException;   
import java.io.Writer;   
import java.util.Iterator;   
import java.util.logging.Logger;

import org.dom4j.Document;   
import org.dom4j.DocumentException;   
import org.dom4j.DocumentHelper;   
import org.dom4j.Element;   
import org.dom4j.io.SAXReader;   
import org.dom4j.io.XMLWriter;   

import com.emaildisk.conf.Config;
import com.emaildisk.network.IMAPHandler;

public class DomXmlHandler {

	static Logger logger = Logger.getLogger(DomXmlHandler.class.getName());
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
		Element content=document.addElement("NodeMetas");   
		Element version=content.addElement("Version");
		version.setText(Config.VERSION);
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
	
	

}
