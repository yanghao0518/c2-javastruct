package com.sgck.dtu.analysis.writer;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.Message;
import com.sgck.dtu.analysis.common.TemplateMessage;

public interface CheckRuleService
{

	public void writeBCC(JSONObject content,TemplateMessage template,Message contentTemplat);
}
