package com.sgck.dtu.analysis.read;

import com.sgck.dtu.analysis.common.Message;

/**
 * 波形特殊处理包
 * @author DELL
 *
 */

public interface ReadWaveMessageService
{

	public Message getMessageContent(Integer packageType);
}
