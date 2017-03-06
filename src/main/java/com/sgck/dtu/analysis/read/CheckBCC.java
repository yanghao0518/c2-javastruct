package com.sgck.dtu.analysis.read;

import java.util.List;

import com.sgck.dtu.analysis.common.Field;
import com.sgck.dtu.analysis.exception.DtuMessageException;

public class CheckBCC implements CheckField {
	
	private String fieldName;
	
	private String method = "default";

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public void check(Field field, CheckFail checkFail, Object... params) throws DtuMessageException{
		if(field.getName().equals(fieldName)){
			if(method.endsWith("default")){
				//异或运算
				List<Byte> orgList = (List<Byte>)params[0];
				if(null == orgList){
					throw new DtuMessageException(DtuMessageException.BBC_CHECK_EXCEPTION,"bbc check fail and org byte collections to bbc not null ");
				}
				int size = orgList.size() - field.getType().BYTES;
				int rt = orgList.get(0);
				if(size>1){
					for(int i=1;i<size;i++){
						rt ^= orgList.get(i);
					}
				}
				if(rt < 0 ){
					rt = rt + 256;
				}
				int bbc = (int)params[1];
				
				if(rt != bbc){
					//校验失败
					if(null != checkFail){
						checkFail.doSomething();
					}else{
						throw new DtuMessageException(DtuMessageException.BBC_CHECK_EXCEPTION,"bbc check fail");
					}
				}
			}else{
				//暂时未定义直接过滤
			}
		}
	}
	
	


}
