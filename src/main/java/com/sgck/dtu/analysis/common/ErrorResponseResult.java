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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getData() {
		// TODO Auto-generated method stub
		return null;
	}

}
