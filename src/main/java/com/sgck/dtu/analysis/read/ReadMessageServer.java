package com.sgck.dtu.analysis.read;

import java.io.IOException;
import java.net.Socket;

import com.sgck.dtu.analysis.exception.DtuMessageException;


public interface ReadMessageServer
{
	public void read(Socket socket) throws IOException,DtuMessageException;
}
