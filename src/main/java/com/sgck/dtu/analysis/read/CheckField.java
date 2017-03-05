package com.sgck.dtu.analysis.read;

import com.sgck.dtu.analysis.common.Field;
import com.sgck.dtu.analysis.exception.DtuMessageException;

public interface CheckField {
	
	public void check(Field field,CheckFail checkFail,Object ... params) throws DtuMessageException;
}
