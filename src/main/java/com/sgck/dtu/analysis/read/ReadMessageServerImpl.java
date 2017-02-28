package com.sgck.dtu.analysis.read;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.ErrorResponseResult;
import com.sgck.dtu.analysis.common.Field;
import com.sgck.dtu.analysis.common.Message;
import com.sgck.dtu.analysis.common.ResponseResult;
import com.sgck.dtu.analysis.exception.DtuMessageException;
import com.sgck.dtu.analysis.manager.HandleMessageManager;
import com.sgck.dtu.analysis.manager.TemplateMessageManager;
import com.sgck.dtu.analysis.writer.ResponseMessageService;
import com.sgck.dtu.analysis.writer.ResponseMessageServiceImpl;

public class ReadMessageServerImpl implements ReadMessageServer
{

	private ReadFieldService analysisFieldService = new ReadFieldServiceImpl();

	private HandleMessageService defaultHandleFcMessageService = new DefaultHandleMessageServiceImpl();

	private ResponseMessageService responseMessageService = new ResponseMessageServiceImpl();
	
	public void setAnalysisFieldService(ReadFieldService analysisFieldService)
	{
		this.analysisFieldService = analysisFieldService;
	}

	public void setDefaultHandleFcMessageService(HandleMessageService defaultHandleFcMessageService)
	{
		this.defaultHandleFcMessageService = new DefaultHandleMessageServiceImpl(defaultHandleFcMessageService);

	}

	@Override
	public void read(Socket socket) throws IOException, DtuMessageException
	{
		analysis(socket);
	}

	public void analysis(Socket socket) throws IOException, DtuMessageException
	{
		// 获取head模板
		Message head = TemplateMessageManager.getInstance().getFCMessageTemplate().getHead();
		Field[] fields = head.getFields();
		int startIndex = 0;
		int endIndex = fields.length;
		JSONObject newjson = new JSONObject();
		InputStream is = socket.getInputStream();
		// 接收前缀包
		for (int i = startIndex; i < endIndex; i++) {
			if (i == startIndex) {
				readFirst(is, fields[i], newjson);
			} else {
				read(is, fields[i], newjson);
			}
		}
		// 判断是具体哪个协议
		Integer packageType = newjson.getInteger(head.getPrimaryKey());
		Message content = TemplateMessageManager.getInstance().getFCMessage(packageType.toString());
		if (content == null) {
			throw new IllegalArgumentException("协议号:" + packageType + "，未在接收端定义!");
		}
		fields = content.getFields();
		endIndex = fields.length;
		// 逐个开始解析
		for (int i = 0; i < endIndex; i++) {
			read(is, fields[i], newjson);
		}
		// 解析foot
		Message foot = TemplateMessageManager.getInstance().getFCMessageTemplate().getFoot();
		if (null != foot) {
			fields = foot.getFields();
			endIndex = fields.length;
			// 逐个开始解析
			for (int i = 0; i < endIndex; i++) {
				read(is, fields[i], newjson);
			}
		}

		// 解析完成 需要根据不同的模板往不同的处理类下发
		HandleMessageService handleService = HandleMessageManager.getInstance().getHandleFcMessageService(content.getId());
		
		//需要返回处理回复
		if(HandleMessageManager.getInstance().getResponseProtocolId(content.getId())){
			ResponseResult result = null;
			if (null == handleService) {
				// 处理具体协议类未找到 默认处理类进行输出并打印日志
				result = defaultHandleMessage(newjson);
			} else {
				result = handleService.handle(newjson);
			}
			//处理错误
			if(result instanceof ErrorResponseResult){
				//暂时做错误输出
				System.out.println(result.getMessage());
				return;
			}
			//result
			JSONObject json = (JSONObject)result.getData();
			byte[] responsedata = responseMessageService.resolve(json.getString("id"),json);
			socket.getOutputStream().write(responsedata);
		}else{
			if (null == handleService) {
				// 处理具体协议类未找到 默认处理类进行输出并打印日志
				defaultHandleMessage(newjson);
			} else {
				handleService.handle(newjson);
			}
		}
		
		

	}

	// 默认处理类
	private ResponseResult defaultHandleMessage(JSONObject json)
	{
		return defaultHandleFcMessageService.handle(json);
	}

	// 读第一个时判断read值是否为-1，如果为-1则表示客户下线需要做下线处理
	private void readFirst(InputStream is, Field field, JSONObject newjson) throws IOException, DtuMessageException
	{
		byte[] recvHead = new byte[field.getType().BYTES];
		int rlRead = is.read(recvHead, 0, field.getType().BYTES);
		if (-1 == rlRead) {
			// 表示已经下线
			throw new DtuMessageException(DtuMessageException.CLIENT_NOT_ONLINE, "this client tcp is not online~");
		}
		newjson.put(field.getName(), analysisFieldService.read(recvHead, field));
	}

	// 此方法需要抽象出去，影响读取方式
	private void read(InputStream is, Field field, JSONObject newjson) throws IOException
	{
		newjson.put(field.getName(), analysisFieldService.read(is, field));
	}

}
