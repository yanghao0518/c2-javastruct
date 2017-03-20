package com.sgck.dtu.analysis.writer;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.LEDataOutputStream;
import com.sgck.dtu.analysis.exception.DtuMessageException;
import com.sgck.dtu.analysis.manager.ClientSocketManager;
import com.sgck.dtu.analysis.manager.ClientThread;

public class SendMessageServiceImpl implements SendMessageService
{

	private ResponseMessageService responseMessageService = new ResponseMessageServiceImpl();

	private SendMessageNewService sendMessageNewService = new SendMessageNewServiceImpl();

	/**
	 * 
	 * @param protocolid
	 *            指定协议ID
	 * @param message
	 *            发送消息体（默认均为JSON格式）
	 */
	public void send(String protocolid, JSONObject content)
	{
		// 返回buffer给socket
		try {
			ClientSocketManager.getInstance().sendMessage(responseMessageService.resolve(protocolid, content));
		} catch (DtuMessageException | IOException e) {
			e.printStackTrace();
		}

	    //return;
//		List<ClientThread> clients = ClientSocketManager.getInstance().getClients();
//		for (ClientThread client : clients) {
//			LEDataOutputStream dos = null;
//			try {
//				dos =  new LEDataOutputStream(client.getSocket().getOutputStream());
//				sendMessageNewService.send(dos, protocolid, content);
//			} catch (DtuMessageException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} finally{
//				if(null != dos){
//					try {
//						dos.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				
//			}
//		}

	}

}
