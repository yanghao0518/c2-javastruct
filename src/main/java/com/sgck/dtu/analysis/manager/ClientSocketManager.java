package com.sgck.dtu.analysis.manager;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.sgck.dtu.analysis.read.ReadMessageServer;
import com.sgck.dtu.analysis.read.ReadMessageServerImpl;

/**
 * 所有客户端socket连接的管理类
 * 
 * @author DELL
 *
 */
public class ClientSocketManager
{
	private List<ClientThread> clients;

	private ReadMessageServer readMessageServer;

	// 默认最多开启10个最大连接客户端
	public final static int max = 10;

	// 初始化
	private ClientSocketManager()
	{
		this.readMessageServer = new ReadMessageServerImpl();
		clients = new ArrayList<ClientThread>();
	}

	private ClientNotOnlineCall callback = new ClientNotOnlineCall()
	{

		@Override
		public void call(ClientThread client) throws IOException
		{
			for (int i = clients.size() - 1; i >= 0; i--) {
				if (clients.get(i).getSocket().getInetAddress().getHostAddress().equals(client.getSocket().getInetAddress().getHostAddress()) && clients.get(i).getSocket().getPort() == client.getSocket().getPort()) {
					clients.get(i).getSocket().close();
					clients.get(i).setClose();
					clients.get(i).stop();// 停止这条服务线程
					clients.remove(i);// 删除此用户的服务线程

				}
			}
		}
	};

	public interface ClientNotOnlineCall
	{
		public void call(ClientThread client) throws IOException;
	}

	public List<ClientThread> getClients()
	{
		return clients;
	}

	public boolean isMax()
	{
		return clients.size() >= max;
	}

	// 发送消息
	public void sendMessage(byte[] msg)
	{
		for (ClientThread client : clients) {
			client.sendMessage(msg);
		}
	}

	public synchronized void addClient(Socket socket)
	{
		ClientThread client = new ClientThread(socket, this.readMessageServer, this.callback);
		client.start();// 开启对此客户端服务的线程
		clients.add(client);
	}

	public synchronized void closeAllClients() throws IOException
	{
		for (int i = clients.size() - 1; i >= 0; i--) {
			// 给所有在线用户发送关闭命令
			// 释放资源
			clients.get(i).setClose();
			clients.get(i).stop();// 停止此条为客户端服务的线程
			clients.get(i).getSocket().close();
			clients.remove(i);
		}
	}

	public static ClientSocketManager getInstance()
	{
		return ClientSocketManagerHolder.instance;
	}

	private static class ClientSocketManagerHolder
	{
		private static ClientSocketManager instance = new ClientSocketManager();
	}

}
