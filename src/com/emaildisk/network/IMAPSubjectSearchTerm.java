package com.emaildisk.network;

import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.search.StringTerm;

public class IMAPSubjectSearchTerm  extends StringTerm{
	static Logger logger = Logger.getLogger(IMAPSubjectSearchTerm.class.getName());
	protected IMAPSubjectSearchTerm(String pattern) {
		super(pattern);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean match(Message arg0) {
		// TODO Auto-generated method stub
		try
		{
			String subject=arg0.getSubject();
			 if(subject == null) return false;
	            return super.match(subject);
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
			return false;
		}
		
	}
	public boolean equals(Object obj) {
          if (! (obj instanceof IMAPSubjectSearchTerm)) {
                return false;
          }
          return super.equals(obj);
    }

}
