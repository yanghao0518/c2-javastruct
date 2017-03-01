package com.sgck.dtu.analysis.common;

public interface ResponseResult {

	public int getCode();
	
	public String getMessage();
	
	public Object getData();
	
	public boolean isReptSend();
	
}
