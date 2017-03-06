package com.sgck.tcp.sgck_dtu_server;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.utiils.HexUtils;

public class Test
{

	public static void main(String[] args)
	{
		String str = HexUtils.getBinaryStrFromByte((byte)17);
		//String str = "00011001";
		
		//截取后三位
				String last = str.substring(str.length()- 3, str.length());
				//截取倒数五位和四位
				String last1 = str.substring(str.length()- 5,str.length()).substring(0, 2);
				
				System.out.println(last);
		System.out.println(last1);
	}
}
