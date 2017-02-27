package com.sgck.dtu.analysis.read;

import java.io.IOException;
import java.io.InputStream;

import com.sgck.dtu.analysis.exception.DtuMessageException;


public interface ReadMessageServer
{
	public void read(InputStream is) throws IOException,DtuMessageException;
}
