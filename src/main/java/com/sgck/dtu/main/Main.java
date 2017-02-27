package com.sgck.dtu.main;

import java.net.BindException;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.manager.HandleMessageManager;
import com.sgck.dtu.analysis.manager.TemplateMessageManager;
import com.sgck.dtu.analysis.utiils.CommonUtils;
import com.sgck.dtu.analysis.writer.SendMessageService;
import com.sgck.dtu.analysis.writer.SendMessageServiceImpl;

public class Main
{
	public static void main(String[] args)
	{
		init();
		try {
			new ReceiveMessageServer().serverStart(9999);
			SendMessageService sendService = new SendMessageServiceImpl();
			JSONObject message = new JSONObject();
			message.put("Gateway_Id", 1234);
			message.put("Package_Number", 25);
			message.put("command_properties", 0x01);
			message.put("BCC", 0x01);
			sendService.send("1", message);
			
		} catch (BindException e) {
			e.printStackTrace();
		};

	}
	
	public static void init(){
		TemplateMessageManager.getInstance();
		HandleMessageManager.getInstance();
	}

	public static byte[] getSendData()
	{
		byte[] temp = null;

		byte[] buffer = new byte[16];

		// add Constant_Down 2b
		temp = CommonUtils.toLH2(43690);
		System.arraycopy(temp, 0, buffer, 0, 2);// 0,1

		// add id 4b
		temp = CommonUtils.toLH(1234);
		System.arraycopy(temp, 0, buffer, 2, 4);// 2,3,4,5

		// add Package_Type 1b
		temp = CommonUtils.toLH1(1);
		System.arraycopy(temp, 0, buffer, 6, 1);// 6

		// add Version 1b
		temp = CommonUtils.toLH1(1);
		System.arraycopy(temp, 0, buffer, 7, 1);// 7

		// add Package_length 2b
		temp = CommonUtils.toLH2(17);
		System.arraycopy(temp, 0, buffer, 8, 2);// 8,9

		// add Package_Number 2b
		temp = CommonUtils.toLH2(291);
		System.arraycopy(temp, 0, buffer, 10, 2);// 10,11

		// add command_ properties 1b
		temp = CommonUtils.toLH1(97);
		System.arraycopy(temp, 0, buffer, 12, 1);// 12

		// BCC 1b
		temp = CommonUtils.toLH1(98);
		System.arraycopy(temp, 0, buffer, 13, 1);// 13

		// Constant_Up_Stop 2b
		temp = CommonUtils.toLH2(43605);
		System.arraycopy(temp, 0, buffer, 14, 2);// 14,15

		return buffer;
	}

}
