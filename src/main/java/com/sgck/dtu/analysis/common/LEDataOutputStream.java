// TODO : This class will be replaced by a new implementation.

package com.sgck.dtu.analysis.common;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class LEDataOutputStream implements DataOutput
{
	protected DataOutputStream d;
	byte w[];
	int writerSize;
	List<Byte> orgList = new ArrayList<>();

	public LEDataOutputStream(OutputStream out)
	{
		this.d = new DataOutputStream(out);
		w = new byte[8]; // work array for composing output
	}
	
	public List<Byte> getOrgList(){
		return orgList;
	}

	/**
	 * like DataOutputStream.writeShort. also acts as a writeUnsignedShort
	 */
	public final void writeShort(int v) throws IOException
	{
		w[0] = (byte) (v & 0xFF);
		w[1] = (byte) ((v >> 8 & 0xFF));
		orgList.add(w[0]);
		orgList.add(w[1]);
		d.write(w, 0, 2);

	}

	/**
	 * like DataOutputStream.writeChar. Note the parm is an int even though this
	 * as a writeChar
	 */
	public final void writeChar(int v) throws IOException
	{
		// same code as writeShort
		w[0] = (byte) (v & 0xFF);
		w[1] = (byte) (v >> 8  & 0xFF);
		orgList.add(w[0]);
		orgList.add(w[1]);
		d.write(w, 0, 2);
	}

	/**
	 * like DataOutputStream.writeInt.
	 */
	public final void writeInt(int v) throws IOException
	{
		w[0] = (byte) (v & 0xFF);
		w[1] = (byte) (v >> 8 & 0xff);
		w[2] = (byte) (v >> 16 & 0xff);
		w[3] = (byte) (v >> 24 & 0xff);
		orgList.add(w[0]);
		orgList.add(w[1]);
		orgList.add(w[2]);
		orgList.add(w[3]);
		d.write(w, 0, 4);
	}

	/**
	 * like DataOutputStream.writeLong.
	 */
	public final void writeLong(long v) throws IOException
	{
		w[0] = (byte) v;
		w[1] = (byte) (v >> 8);
		w[2] = (byte) (v >> 16);
		w[3] = (byte) (v >> 24);
		w[4] = (byte) (v >> 32);
		w[5] = (byte) (v >> 40);
		w[6] = (byte) (v >> 48);
		w[7] = (byte) (v >> 56);
		orgList.add(w[0]);
		orgList.add(w[1]);
		orgList.add(w[2]);
		orgList.add(w[3]);
		orgList.add(w[4]);
		orgList.add(w[5]);
		orgList.add(w[6]);
		orgList.add(w[7]);
		d.write(w, 0, 8);
	}

	/**
	 * like DataOutputStream.writeFloat.
	 */
	public final void writeFloat(float v) throws IOException
	{
		writeInt(Float.floatToIntBits(v));
	}

	/**
	 * like DataOutputStream.writeDouble.
	 */
	public final void writeDouble(double v) throws IOException
	{
		writeLong(Double.doubleToLongBits(v));
	}

	/**
	 * like DataOutputStream.writeChars, flip each char.
	 */
	public final void writeChars(String s) throws IOException
	{
		int len = s.length();
		for (int i = 0; i < len; i++) {
			writeChar(s.charAt(i));
		}
	} // end writeChars

	// p u r e l y w r a p p e r m e t h o d s
	// We cannot inherit since DataOutputStream is final.

	/* This method writes only one byte, even though it says int */
	public final synchronized void write(int b) throws IOException
	{
		d.write(b);
	}

	public final synchronized void write(byte b[], int off, int len) throws IOException
	{
		d.write(b, off, len);
	}

	public void flush() throws IOException
	{
		d.flush();
	}

	/* Only writes one byte */
	public final void writeBoolean(boolean v) throws IOException
	{
		orgList.add(Boolean.toString(v).getBytes()[0]);
		d.writeBoolean(v);
	}

	public final void writeByte(int v) throws IOException
	{
		byte tmp = (byte)( v & 0xFF);
		orgList.add(tmp);
		d.writeByte(tmp);
	}

	public final void writeBytes(String s) throws IOException
	{
		for (byte b : s.getBytes()) {
			orgList.add(b);
		}
		d.writeBytes(s);
	}

	public final void writeUTF(String str) throws IOException
	{
		for (byte b : str.getBytes()) {
			orgList.add(b);
		}
		d.writeUTF(str);
	}

	public final int size()
	{
		return d.size();
	}

	public final void write(byte b[]) throws IOException
	{
		d.write(b, 0, b.length);
	}

	public final void close() throws IOException
	{
		d.close();
	}

}
