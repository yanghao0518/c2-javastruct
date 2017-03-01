package com.sgck.tcp.sgck_dtu_server;

import com.alibaba.fastjson.JSONObject;

public class Test
{

	public static void main(String[] args)
	{
		JSONObject json = new JSONObject();
		int b = 100;
		json.put("a", 1);
		json.put("b", b);
		
		System.out.println(json.getIntValue("a")/json.getDoubleValue("b"));
	}
}
