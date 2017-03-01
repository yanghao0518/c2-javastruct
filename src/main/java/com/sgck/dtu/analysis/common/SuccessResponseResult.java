package com.sgck.dtu.analysis.common;

public class SuccessResponseResult implements ResponseResult{

	private final static int code = 200;
	private boolean isreptSend ;
	private Object data;
	
	public SuccessResponseResult(Object data){
		this.data = data;
	}
	
	public SuccessResponseResult(Object data,boolean isreptSend){
		this.data = data;
		this.isreptSend = isreptSend;
	}
	
	@Override
	public int getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return null;
	}

	@Override
	public Object getData() {
		return data;
	}

	@Override
	public boolean isReptSend()
	{
		return isreptSend;
	}

}
