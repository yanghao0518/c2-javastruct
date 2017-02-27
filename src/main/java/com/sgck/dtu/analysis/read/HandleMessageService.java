package com.sgck.dtu.analysis.read;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.ResponseResult;

/**
 * 处理接受来自C＋＋消息结构服务类
 * @author yanghao0518
 *
 */
public interface HandleMessageService {

	public ResponseResult handle(JSONObject message);
}
