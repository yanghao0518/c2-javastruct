package com.sgck.dtu.test;

public class MsgHeader {
	private int cmd;
	private int length;
	private int para1;
	private int para2;
	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPara1() {
		return para1;
	}

	public void setPara1(int para1) {
		this.para1 = para1;
	}

	public int getPara2() {
		return para2;
	}

	public void setPara2(int para2) {
		this.para2 = para2;
	}

	public int getPara3() {
		return para3;
	}

	public void setPara3(int para3) {
		this.para3 = para3;
	}

	private int para3;
	
	public MsgHeader()
	{
		this.cmd = 0;
		this.length = 0;
		this.para1 = 0;
		this.para2 = 0;
		this.para3 = 0;
	}

	public MsgHeader(int cmd,int length,int para1,int para2,int para3){
		this.cmd=cmd;
		this.length= length;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
	}
}
