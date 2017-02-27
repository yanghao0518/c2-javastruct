package com.sgck.dtu.analysis.utiils;

import java.util.Collection;

public class CheckUtils
{

	public static void checkNull(Object value, String message) throws IllegalArgumentException
	{
		if (null == value) {
			throw new IllegalArgumentException(message);
		}
		if (value instanceof String) {
			if (((String) value).length() == 0) {
				throw new IllegalArgumentException(message);
			}
		}
	}
	
	public static void checkEmpty(Object value, String message) throws IllegalArgumentException
	{
		if (null == value) {
			throw new IllegalArgumentException(message);
		}
		if (value instanceof Collection) {
			if (((Collection) value).isEmpty()) {
				throw new IllegalArgumentException(message);
			}
		}
	}
}
