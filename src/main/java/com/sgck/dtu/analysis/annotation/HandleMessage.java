package com.sgck.dtu.analysis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消息处理类
 * 
 * @author DELL
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface HandleMessage {

	public enum HandleType {
		RECEIVE("处理接收的消息"), SEND("处理发送的消息");
		private String desc;

		private HandleType(String desc)
		{
			this.setDesc(desc);
		}

		public String getDesc()
		{
			return desc;
		}

		public void setDesc(String desc)
		{
			this.desc = desc;
		}
	}

	HandleType Type();

}
