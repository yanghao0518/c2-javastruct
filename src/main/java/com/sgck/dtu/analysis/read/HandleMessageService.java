package com.sgck.dtu.analysis.read;

import com.alibaba.fastjson.JSONObject;

/**
 * 处理接受来自C＋＋消息结构服务类
 * @author yanghao0518
 *
 */
public interface HandleMessageService {

	public void handle(JSONObject message);
}
