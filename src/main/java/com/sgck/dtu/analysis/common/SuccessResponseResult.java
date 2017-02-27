package com.sgck.dtu.analysis.common;

public class SuccessResponseResult implements ResponseResult{

	private final static int code = 200;
	
	private Object data;
	public SuccessResponseResult(Object data){
		this.data = data;
	}
	
	@Override
	public int getCode() {
		// TODO Auto-generated method stub
		return code;
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
