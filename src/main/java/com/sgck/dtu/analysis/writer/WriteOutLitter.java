package com.sgck.dtu.analysis.writer;

import java.io.IOException;
import java.util.List;

public class WriteOutLitter implements WriteOut
{

	public void writeShort(List<Byte> list, int v) throws IOException
	{
		list.add((byte) (v & 0xFF));
		list.add((byte) ((v >> 8 & 0xFF)));
	}

	/**
	 * like DataOutputStream.writeChar. Note the parm is an int even though this
	 * as a writeChar
	 */
	public void writeChar(List<Byte> list, int v) throws IOException
	{
		// same code as writeShort
		list.add((byte) (v & 0xFF));
		list.add((byte) ((v >> 8 & 0xFF)));
	}

	/**
	 * like DataOutputStream.writeInt.
	 */
	public void writeInt(List<Byte> list, int v) throws IOException
	{

		list.add((byte) (v & 0xFF));
		list.add((byte) ((v >> 8 & 0xFF)));
		list.add((byte) ((v >> 16 & 0xFF)));
		list.add((byte) ((v >> 24 & 0xFF)));

	}

	public void writeUInt(List<Byte> list, long v) throws IOException
	{

		list.add((byte) (v & 0xFF));
		list.add((byte) ((v >> 8 & 0xFF)));
		list.add((byte) ((v >> 16 & 0xFF)));
		list.add((byte) ((v >> 24 & 0xFF)));

	}

	/**
	 * like DataOutputStream.writeLong.
	 */
	public void writeLong(List<Byte> list, long v) throws IOException
	{

		list.add((byte) (v & 0xFF));
		list.add((byte) ((v >> 8 & 0xFF)));
		list.add((byte) ((v >> 16 & 0xFF)));
		list.add((byte) ((v >> 24 & 0xFF)));

		list.add((byte) ((v >> 32 & 0xFF)));
		list.add((byte) ((v >> 40 & 0xFF)));
		list.add((byte) ((v >> 48 & 0xFF)));
		list.add((byte) ((v >> 56 & 0xFF)));

	}

	/**
	 * like DataOutputStream.writeFloat.
	 */
	public void writeFloat(List<Byte> list, float v) throws IOException
	{
		writeInt(list, Float.floatToIntBits(v));
	}

	/**
	 * like DataOutputStream.writeDouble.
	 */
	public void writeDouble(List<Byte> list, double v) throws IOException
	{
		writeLong(list, Double.doubleToLongBits(v));
	}

	/**
	 * like DataOutputStream.writeChars, flip each char.
	 */
	public void writeChars(List<Byte> list, String s) throws IOException
	{
		int len = s.length();
		for (int i = 0; i < len; i++) {
			writeChar(list, s.charAt(i));
		}
	} // end writeChars

	/* Only writes one byte */
	public void writeBoolean(List<Byte> list, boolean v) throws IOException
	{
		list.add((byte) (v ? 1 : 0));
	}

	public void writeByte(List<Byte> list, int v) throws IOException
	{
		list.add((byte) (v & 0xFF));
	}

	public void writeString(List<Byte> list, String s) throws IOException
	{
		for (byte b : s.getBytes()) {
			list.add(b);
		}
	}

	public void writeString(List<Byte> list, String s, String encoding) throws IOException
	{
		for (byte b : s.getBytes(encoding)) {
			list.add(b);
		}
	}

}
