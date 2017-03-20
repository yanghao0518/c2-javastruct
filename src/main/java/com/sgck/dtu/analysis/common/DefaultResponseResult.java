package com.sgck.dtu.analysis.common;

import com.alibaba.fastjson.JSONObject;

public interface DefaultResponseResult extends ResponseResult
{

	public void setRtnData(JSONObject message);
}
