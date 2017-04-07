package com.sgck.dtu.analysis.read;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.sgck.dtu.analysis.exception.DtuMessageException;

public interface ReadInput
{
	public void readFully(InputStream in, byte b[], int off, int len) throws IOException, DtuMessageException;

	public void readToList(byte[] w, List<Byte> list);

	public short readShort(InputStream in, List<Byte> list) throws IOException, DtuMessageException;

	public int readUnsignedShort(InputStream in, List<Byte> list) throws IOException;

	public char readChar(InputStream in, List<Byte> list) throws IOException;

	public int readInt(InputStream in, List<Byte> list) throws DtuMessageException, IOException;

	public long readUnsignedInt(InputStream in, List<Byte> list) throws DtuMessageException, IOException;;

	public long readLong(InputStream in, List<Byte> list) throws DtuMessageException, IOException;;

	public float readFloat(InputStream in, List<Byte> list) throws DtuMessageException, IOException;;

	public double readDouble(InputStream in, List<Byte> list) throws DtuMessageException, IOException;;

	public boolean readBoolean(InputStream in, List<Byte> list) throws DtuMessageException, IOException;;

	public byte readUnsignedByte(InputStream in, List<Byte> list) throws DtuMessageException, IOException;;

	public int readCharC(InputStream in, List<Byte> list) throws DtuMessageException, IOException;;

	public int readByte(InputStream in, List<Byte> list) throws DtuMessageException, IOException;;

	public void readShortArray(InputStream in, short[] datas, List<Byte> list) throws DtuMessageException, IOException;;

	public void readUShortArray(InputStream in, int[] datas, List<Byte> list) throws DtuMessageException, IOException;;

	public void readUShortArray(InputStream in, int[] datas, List<Byte> list,List<Number> wavePackage) throws DtuMessageException, IOException;;

	
	public String readUTF(InputStream in, int len, List<Byte> list) throws DtuMessageException, IOException;;

	public String readUTF(InputStream in, int len, List<Byte> list, String encoding)throws DtuMessageException, IOException;;

	public long getLong(byte buf[], int index);

}
