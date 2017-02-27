package com.sgck.dtu.main;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import com.sgck.dtu.analysis.manager.ClientSocketManager;
import com.sgck.dtu.analysis.manager.ClientThread;

/**
 * 接收消息服务
 * 
 * @author DELL
 *
 */
public class ReceiveMessageServer
{

	private ServerSocket serverSocket;

	private ServerThread serverThread;

	private boolean isStart = false;

	public boolean isClose = false;

	// 启动服务器
	public void serverStart(int port) throws java.net.BindException
	{
		try {
			serverSocket = new ServerSocket(port);
			serverThread = new ServerThread(serverSocket);
			serverThread.start();
			isStart = true;
			// 开启检测线程
			CheckSocketClientThread checkThread = new CheckSocketClientThread();
			checkThread.start();
		} catch (BindException e) {
			isStart = false;
			throw new BindException("端口号已被占用，请换一个！");
		} catch (Exception e1) {
			e1.printStackTrace();
			isStart = false;
			throw new BindException("启动服务器异常！");
		}
	}

	// 关闭服务器
	@SuppressWarnings("deprecation")
	public void closeServer()
	{
		try {
			if (serverThread != null) {
				serverThread.stop();// 停止服务器线程
			}

			ClientSocketManager.getInstance().closeAllClients();

			if (serverSocket != null) {
				serverSocket.close();// 关闭服务器端连接
			}
		} catch (IOException e) {
			e.printStackTrace();
			isStart = true;
		} finally {
			isStart = true;
			isClose = true;
		}
	}

	// 服务器线程
	class ServerThread extends Thread
	{
		private ServerSocket serverSocket;

		// 服务器线程的构造方法
		public ServerThread(ServerSocket serverSocket)
		{
			this.serverSocket = serverSocket;
		}

		public void run()
		{
			while (!isClose) {// 不停的等待客户端的链接
				try {
					Socket socket = serverSocket.accept();
					if (ClientSocketManager.getInstance().isMax()) {// 如果已达人数上限
						System.out.println("服务器在线人数已达上限，请稍后尝试连接!");
						socket.close();
						continue;
					}
					ClientSocketManager.getInstance().addClient(socket);
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 实时检查客户端socket是否断掉
	class CheckSocketClientThread extends Thread
	{

		@Override
		public void run()
		{
			while (!isClose) {
				for (ClientThread client : ClientSocketManager.getInstance().getClients()) {
					if (isServerClose(client.getSocket())) {
						client.autoRemove();
					}
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

		/**
		 * 判断是否断开连接，断开返回true,没有返回false
		 * 
		 * @param socket
		 * @return
		 */
		private Boolean isServerClose(Socket socket)
		{
			try {
				socket.sendUrgentData(0xFF);// 发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
				return false;
			} catch (Exception se) {
				return true;
			}
		}
	}
}
