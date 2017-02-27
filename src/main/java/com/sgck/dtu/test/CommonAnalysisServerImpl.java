package com.sgck.dtu.test;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.sgck.dtu.analysis.utiils.CommonUtils;



public class CommonAnalysisServerImpl implements CommonAnalysisServer
{

	@Override
	public void analysisDtuMessage(InputStream is) throws IOException
	{

			test1(is);
	
		
	}
	
	private void test2(InputStream is) throws IOException{
		byte[] data = new byte[16];
		is.read(data);
//		ConfirmationMessage m = new ConfirmationMessage();
//		try {
//			JavaStruct.unpack(m, data);
//			System.out.println(m);
//		} catch (StructException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		DataInput di = new DataInputStream(new ByteArrayInputStream(data));
		
		int i;
		char l;
		long c;
		byte b;
		//di.readFully(b, off, len);
		
		i = di.readUnsignedShort();
		b = di.readByte();
		b = di.readByte();
		b = di.readByte();
		b = di.readByte();
		b = di.readByte();
		b = di.readByte();
		b = di.readByte();
		b = di.readByte();
		b = di.readByte();
		c = di.readInt();
		l = di.readChar();
		l = di.readChar();
		i = di.readUnsignedShort();
		i = di.readUnsignedShort();
		l = di.readChar();
		l = di.readChar();
		i = di.readUnsignedShort();
	}
	
	private void test1(InputStream is) throws IOException{
			byte[] recvHead = new byte[4];
			byte[] recvHead2 = new byte[2];
			
			byte[] recvHead3 = new byte[4];
			int cForm;
			int cmd, length, para1, para2, para3;

			is.read(recvHead2, 0, 2);
			
			
		cForm = CommonUtils.bytes2Integer(recvHead2);
			cmd = CommonUtils.bytes2Integer(CommonUtils.toLH(cForm));

			// read length
			recvHead = new byte[4];
			is.read(recvHead, 0, 4);
			cForm = CommonUtils.bytes2Integer(recvHead);
			length = CommonUtils.bytes2Integer(CommonUtils.toLH(cForm));
			
			byte[] recvHead1 = new byte[1];
			is.read(recvHead1, 0, 1);
			cForm = CommonUtils.bytes2Integer(recvHead1);
			length = CommonUtils.bytes2Integer(CommonUtils.toLH(cForm));
			
			is.read(recvHead1, 0, 1);
			cForm = CommonUtils.bytes2Integer(recvHead1);
			length = CommonUtils.bytes2Integer(CommonUtils.toLH(cForm));
			
			is.read(recvHead, 0, 2);
			cForm = CommonUtils.bytes2Integer(recvHead);
			length = CommonUtils.bytes2Integer(CommonUtils.toLH(cForm));
			
			is.read(recvHead, 0, 2);
			cForm = CommonUtils.bytes2Integer(recvHead);
			length = CommonUtils.bytes2Integer(CommonUtils.toLH(cForm));
			
			is.read(recvHead1,0, 1);
			cForm = CommonUtils.bytes2Integer(recvHead1);
			length = CommonUtils.bytes2Integer(CommonUtils.toLH(cForm));
			
			is.read(recvHead1,0, 1);
			cForm = CommonUtils.bytes2Integer(recvHead1);
			length = CommonUtils.bytes2Integer(CommonUtils.toLH(cForm));
			
			is.read(recvHead, 0, 2);
			cForm = CommonUtils.bytes2Integer(recvHead);
			length = CommonUtils.bytes2Integer(CommonUtils.toLH(cForm));
			
			
			//System.out.println(length);
	}

}
