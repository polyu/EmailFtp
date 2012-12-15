package com.emaildisk.io;

import java.security.MessageDigest;

public class HashCalc {
	public static final char[] hexChar = {
		'0', '1', '2', '3',
		'4', '5', '6', '7',
		'8', '9', 'a', 'b',
		'c', 'd', 'e', 'f'};
	public static String toHexString(byte[] b) 
	{
		if(b==null) return null;
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
		sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
		sb.append(hexChar[b[i] & 0x0f]);
		}
		return sb.toString();
	}
	public static String calcHash(byte[] data)
	{
		if(data==null) return null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(data);
			return toHexString(md.digest());
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
}
