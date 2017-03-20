package com.sgck.dtu.analysis.writer;

import java.io.IOException;
import java.util.List;

public interface WriteOut
{
	public void writeShort(List<Byte> list, int v) throws IOException;

	public void writeChar(List<Byte> list, int v) throws IOException;

	public void writeInt(List<Byte> list, int v) throws IOException;

	public void writeUInt(List<Byte> list, long v) throws IOException;

	public void writeLong(List<Byte> list, long v) throws IOException;

	public void writeFloat(List<Byte> list, float v) throws IOException;

	public void writeDouble(List<Byte> list, double v) throws IOException;

	public void writeChars(List<Byte> list, String s) throws IOException;

	public void writeBoolean(List<Byte> list, boolean v) throws IOException;

	public void writeByte(List<Byte> list, int v) throws IOException;

	public void writeString(List<Byte> list, String s) throws IOException;

	public void writeString(List<Byte> list, String s, String encoding) throws IOException;

}
