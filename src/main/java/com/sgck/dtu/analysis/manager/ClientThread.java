package com.sgck.dtu.analysis.manager;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

import com.sgck.dtu.analysis.common.LEDataInputStream;
import com.sgck.dtu.analysis.exception.DtuMessageException;
import com.sgck.dtu.analysis.read.C2DataInput;
import com.sgck.dtu.analysis.read.ReadMessageServer;

public class ClientThread extends Thread
{

	private Socket socket;
	private boolean iscolse;
	private ReadMessageServer readMessageServer;
	//private ClientNotOnlineCall callback;
	private C2DataInput lis;
	Logger LOG = Logger.getLogger(this.getClass());

	public Socket getSocket()
	{
		return socket;
	}

	public void setClose()
	{
		iscolse = true;
	}

	public void sendMessage(byte[] data)
	{
		try {
			this.socket.getOutputStream().write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getPrintInfo()
	{
		return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
	}

	// 客户端线程的构造方法
	public ClientThread(Socket socket, ReadMessageServer readMessageServer)
	{
		this.socket = socket;
		this.readMessageServer = readMessageServer;
		//this.callback = callback;
		try {
			lis = new LEDataInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 打印连接成功信息
		LOG.info(getPrintInfo() + "与服务器连接成功!");
	}

	public void run()
	{// 不断接收客户端的消息，进行处理。
		while (!iscolse) {
			try {
				readMessageServer.read(this.socket.getInputStream(), this.socket.getOutputStream());
			} catch (SocketException e) {
				// 断开连接释放资源
				LOG.info(getPrintInfo() + ",已经断开连接!");
				if (!this.iscolse) {
					try {
						this.socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				// 此时socket被重置或者自动断开需要重新连接
				autoRemove();

			} catch (DtuMessageException e) {
				if (e.isNotOnline()) {
					LOG.info(getPrintInfo() + ",已经断开连接!");
					// 断开连接释放资源
					if (!this.iscolse) {
						try {
							this.socket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					autoRemove();
				}
			} catch (EOFException e) {
				LOG.info(getPrintInfo() + ",已经断开连接!");
				// 断开连接释放资源
				if (!this.iscolse) {
					try {
						this.socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				autoRemove();
			} catch (IOException e) {
				LOG.error(e.getMessage(),e);
			}
			 catch (Exception e) {
				 LOG.error(e.getMessage(),e);
			}
		}
	}

	// 自动删除断掉的socket
	// 自动删除本线程
	public void autoRemove()
	{
		try {
			//this.callback.call(this);
			if (null != lis) {
				lis.close();
			}
			iscolse = true;
		} catch (IOException e) {
			e.printStackTrace();
			iscolse = false;
		}
	}
}
