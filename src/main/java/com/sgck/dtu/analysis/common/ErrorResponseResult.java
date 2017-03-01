package com.sgck.dtu.analysis.common;

public class ErrorResponseResult implements ResponseResult {
	
	private int code;
	
	private String message;
	
	private Object data;
	
	public ErrorResponseResult(int code,String message){
		this.code = code;
		this.message = message;
	}

	@Override
	public int getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public Object getData() {
		return this.data;
	}

	@Override
	public boolean isReptSend()
	{
		return false;
	}

}
