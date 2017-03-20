package com.sgck.dtu.analysis.read;

import java.io.DataInput;
import java.io.IOException;
import java.util.List;

public interface C2DataInput extends DataInput
{
	public void readOrgList(List<Byte> list);
	public void close() throws IOException;
	public int readCharC() throws IOException;
	public int readUnsignedInt() throws IOException;
	public void readUShortArray(int[] datas,List<Byte>orgList) throws IOException;
	public int read(byte b[], int off, int len) throws IOException;
}
