package com.sgck.dtu.test;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.sgck.dtu.analysis.utiils.CommonUtils;

public class DBCAgent {
	private Socket socket = null;
	private byte[] buffer;

	public DBCAgent() {
		try {
			this.socket = new Socket("192.168.9.64", 8886);

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void recvData() throws IOException {
		byte[] recvHead = new byte[4];
		int cForm;
		int cmd, length, para1, para2, para3;

		// read cmd
		socket.getInputStream().read(recvHead, 0, 4);
		cForm = CommonUtils.bytes2Integer(recvHead);
		cmd = CommonUtils.bytes2Integer(CommonUtils.toLH(cForm));

		// read length
		socket.getInputStream().read(recvHead, 0, 4);
		cForm = CommonUtils.bytes2Integer(recvHead);
		length = CommonUtils.bytes2Integer(CommonUtils.toLH(cForm));

		// read para1
		socket.getInputStream().read(recvHead, 0, 4);
		cForm = CommonUtils.bytes2Integer(recvHead);
		para1 = CommonUtils.bytes2Integer(CommonUtils.toLH(cForm));

		// read para2
		socket.getInputStream().read(recvHead, 0, 4);
		cForm = CommonUtils.bytes2Integer(recvHead);
		para2 = CommonUtils.bytes2Integer(CommonUtils.toLH(cForm));

		// read para3
		socket.getInputStream().read(recvHead, 0, 4);
		cForm = CommonUtils.bytes2Integer(recvHead);
		para3 = CommonUtils.bytes2Integer(CommonUtils.toLH(cForm));

		byte[] recvData = new byte[length];

		// read data
		socket.getInputStream().read(recvData, 0, length);
		String DataStr = new String(recvData);

		System.out.println(DataStr);
	}

	public boolean sendData(String data) {
		byte[] temp = null;
		MsgHeader header = new MsgHeader();
		header.setLength(data.length());

		this.buffer = new byte[data.length() + 20];

		// add cmd
		temp = CommonUtils.toLH(Protocol.PORTAL_DBC_INSTALLSYSTEM);
		System.arraycopy(temp, 0, buffer, 0, 4);

		// add lenght
		temp = CommonUtils.toLH(header.getLength());
		System.arraycopy(temp, 0, buffer, 4, 4);

		// add para1
		temp = CommonUtils.toLH(header.getPara1());
		System.arraycopy(temp, 0, buffer, 8, 4);

		// add para2
		temp = CommonUtils.toLH(header.getPara2());
		System.arraycopy(temp, 0, buffer, 12, 4);

		// add para3
		temp = CommonUtils.toLH(header.getPara3());
		System.arraycopy(temp, 0, buffer, 16, 4);

		System.arraycopy(data.getBytes(), 0, buffer, 20, data.length());

		try {
			socket.getOutputStream().write(this.buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}
}
