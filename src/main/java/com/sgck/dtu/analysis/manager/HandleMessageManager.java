package com.sgck.dtu.analysis.manager;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.annotation.HandleMessage;
import com.sgck.dtu.analysis.annotation.HandleMessageProtocol;
import com.sgck.dtu.analysis.annotation.HandleMessage.HandleType;
import com.sgck.dtu.analysis.common.TemplateMessage;
import com.sgck.dtu.analysis.read.HandleMessageService;
import com.sgck.dtu.analysis.utiils.ClassUtils;

/**
 * 处理消息管理类
 * 
 * @author yanghao0518
 *
 */
public class HandleMessageManager
{

	private Map<String, HandleMessageService> handleMessageServiceMapping;
	
	private HandleMessageManager()
	{
		try {
			initHandleFcMessageService();
		} catch (Throwable t) {
			// 打印输出
			t.printStackTrace();
			System.exit(0);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initHandleFcMessageService() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException
	{
		handleMessageServiceMapping = new HashMap<String, HandleMessageService>();
		// 首先有模版管理类负责解析模版
		// 获取FC模版实例
		TemplateMessage FCMessageTemplate = TemplateMessageManager.getInstance().getFCMessageTemplate();
		// 如果未定义任何协议则不继续
		if (FCMessageTemplate.getTemplates().isEmpty()) {
			return;
		}

		// 获取基础包路径集合
		List<String> basePackages = FCMessageTemplate.getBasePackages();
		if (basePackages.isEmpty()) {
			// 基础包路径不能未定义
			throw new IllegalArgumentException("basePackages not allow undefined or not set src!");
		}

		for (String packageName : basePackages) {
			// 获取指定包下面的所有实现类
			List<Class> listClasses = ClassUtils.getAllClassByInterface(HandleMessageService.class, packageName);
			if (null == listClasses || listClasses.isEmpty()) {
				continue;
			}
			for (Class invoke : listClasses) {
				HandleMessage annotation = (HandleMessage) invoke.getAnnotation(HandleMessage.class);
				// 不仅是HandleMessage注解，且处理类型为接受消息类型
				if (null != annotation && annotation.Type() == HandleType.RECEIVE) {
					// 获取方法注解的ID参数
					Method method = invoke.getDeclaredMethod("handle", JSONObject.class);
					HandleMessageProtocol protocol = method.getAnnotation(HandleMessageProtocol.class);
					if (null == protocol) {
						// 凡是已经注解了接受类型class其实现方法必须含有HandleMessageProtocol注解
						throw new IllegalArgumentException(invoke.getSimpleName() + "'method:" + method.getName() + " not defined HandleMessageProtocol annotation");
					}
					// 检查协议ID是否在xml定义中有
					String protocolId = protocol.id();
					if (FCMessageTemplate.getMessage(protocolId) == null) {
						throw new IllegalArgumentException(invoke.getSimpleName() + "'method:" + method.getName() + "'HandleMessageProtocol annotation id=" + protocolId + " not defined in FC.xml or TC.xml");
					}
					if (handleMessageServiceMapping.containsKey(protocolId)) {
						throw new IllegalArgumentException("repeated HandleMessageProtocol annotation id=" + protocolId);
					}
					handleMessageServiceMapping.put(protocolId, (HandleMessageService) invoke.newInstance());
				}

			}
		}
	}

	public HandleMessageService getHandleFcMessageService(String protocolId)
	{
		return handleMessageServiceMapping.get(protocolId);
	}

	public static HandleMessageManager getInstance()
	{
		return HandleMessageManagerHolder.instance;
	}

	private static class HandleMessageManagerHolder
	{
		private static HandleMessageManager instance = new HandleMessageManager();
	}
}
