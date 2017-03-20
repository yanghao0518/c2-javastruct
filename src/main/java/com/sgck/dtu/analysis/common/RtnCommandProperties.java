package com.sgck.dtu.analysis.common;

public enum RtnCommandProperties {
	TEST(1,"测试通信"),
	SUCCESS(2,"数据接收正确"),
	ERROR_AND_RETRY(3,"数据接收错误，要求重发"),
	ERROR_NOT_RETRY(4,"数据接收错误，不要求重发"),
	END_TRANSFER(5,"终止传输（多包传输时使用）"),
	ACCEPT_SUCCESS_COMMAND_CANNOT_EXECUTE(6,"接收正确，命令无法执行");
	public int ID;// 对应返回ID
	public String DESC;// 描述

	private RtnCommandProperties(int ID, String DESC)
	{
		this.ID = ID;
		this.DESC = DESC;
	}
}
