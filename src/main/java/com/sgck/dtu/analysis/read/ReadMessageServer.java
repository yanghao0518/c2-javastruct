package com.sgck.dtu.analysis.read;

import java.io.InputStream;
import java.io.OutputStream;



public interface ReadMessageServer
{
	public void read(InputStream in,OutputStream os) throws Exception;
}
