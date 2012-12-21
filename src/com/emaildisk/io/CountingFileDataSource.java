package com.emaildisk.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.FileDataSource;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;

import com.emaildisk.network.NetworkListener;

public class CountingFileDataSource extends FileDataSource{
	NetworkListener nl=null;
	public CountingFileDataSource(File arg0,NetworkListener nl) {
		super(arg0);
		this.nl=nl;
		// TODO Auto-generated constructor stub
	}
	public CountingFileDataSource(String arg0,NetworkListener nl) {
		super(arg0);
		this.nl=nl;
		// TODO Auto-generated constructor stub
	}
	@Override
	public InputStream getInputStream() throws IOException
	{
		return new CountingInputStream(super.getInputStream())
		{
			@Override protected void afterRead(int n)
			{
				super.afterRead(n);
				if(nl!=null)
				nl.bytesSent(n);
			}
		};
	}

}
