package com.sgck.dtu.analysis.common;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.sgck.dtu.analysis.exception.DtuMessageException;
import com.sgck.dtu.analysis.read.C2DataInput;

//TODO : This class will be replaced by a new, more efficent implementation.

public class LEDataInputStream implements C2DataInput
{
	private DataInputStream d;
	private InputStream in;
	byte w[];
	int readSize;

	public void readOrgList(List<Byte> list)
	{
		int i = readSize;
		while (i > 0) {
			list.add(w[i - 1]);
			i--;
		}

	}

	public LEDataInputStream(InputStream in)
	{
		this.in = in;
		this.d = new DataInputStream(in);
		w = new byte[8];
	}

	/**
	 * like DataInputStream.readShort except little endian.
	 */
	public final short readShort() throws IOException
	{
		readSize = 2;
		d.readFully(w, 0, 2);

		return (short) ((w[1] & 0xff) << 8 | (w[0] & 0xff));
	}

	/**
	 * like DataInputStream.readUnsignedShort except little endian. Note,
	 * returns int even though it reads a short.
	 */
	public final int readUnsignedShort() throws IOException
	{
		readSize = 2;
		d.readFully(w, 0, 2);
		return ((w[1] & 0xff) << 8 | (w[0] & 0xff));
	}

	/**
	 * like DataInputStream.readChar except little endian.
	 */
	public final char readChar() throws IOException
	{
		readSize = 2;
		d.readFully(w, 0, 2);
		return (char) ((w[1] & 0xff) << 8 | (w[0] & 0xff));
	}

	/**
	 * like DataInputStream.readInt except little endian.
	 */
	public final int readInt() throws IOException
	{
		readSize = 4;
		d.readFully(w, 0, 4);
		return (w[3] & 0xff) << 24 | (w[2] & 0xff) << 16 | (w[1] & 0xff) << 8 | (w[0] & 0xff);
	}

	public final int readUnsignedInt() throws IOException
	{
		readSize = 4;
		d.readFully(w, 0, 4);
		return (w[3] & 0xff) << 24 | (w[2] & 0xff) << 16 | (w[1] & 0xff) << 8 | (w[0] & 0xff);
	}

	/**
	 * like DataInputStream.readLong except little endian.
	 */
	public final long readLong() throws IOException
	{
		readSize = 8;
		d.readFully(w, 0, 8);
		return (long) (w[7] & 0xff) << 56
				| /* long cast needed or shift done modulo 32 */
				(long) (w[6] & 0xff) << 48 | (long) (w[5] & 0xff) << 40 | (long) (w[4] & 0xff) << 32 | (long) (w[3] & 0xff) << 24 | (long) (w[2] & 0xff) << 16 | (long) (w[1] & 0xff) << 8 | (long) (w[0] & 0xff);
	}

	/**
	 * like DataInputStream.readFloat except little endian.
	 */
	public final float readFloat() throws IOException
	{
		return Float.intBitsToFloat(readInt());
	}

	/**
	 * like DataInputStream.readDouble except little endian.
	 */
	public final double readDouble() throws IOException
	{
		return Double.longBitsToDouble(readLong());
	}

	/* Watch out, may return fewer bytes than requested. */
	public final int read(byte b[], int off, int len) throws IOException
	{
		// For efficiency, we avoid one layer of wrapper
		return in.read(b, off, len);
	}

	public final void readFully(byte b[]) throws IOException
	{
		d.readFully(b, 0, b.length);
	}

	public final void readFully(byte b[], int off, int len) throws IOException
	{
		d.readFully(b, off, len);
	}

	/**
	 * See the general contract of the <code>skipBytes</code> method of
	 * <code>DataInput</code>.
	 * <p>
	 * Bytes for this operation are read from the contained input stream.
	 *
	 * @param n
	 *            the number of bytes to be skipped.
	 * @return the actual number of bytes skipped.
	 * @exception IOException
	 *                if an I/O error occurs.
	 */
	public final int skipBytes(int n) throws IOException
	{
		return d.skipBytes(n);
	}

	/* only reads one byte */
	public final boolean readBoolean() throws IOException
	{
		readSize = 1;
		d.readFully(w, 0, 1);
		if (w[0] < 0)
			throw new IllegalArgumentException();
		return w[0] != 0;
	}

	public final byte readByte() throws IOException
	{

		readSize = 1;
		d.readFully(w, 0, 1);
		if (w[0] < 0)
			throw new IllegalArgumentException();
		return (byte) w[0];
	}

	public final int readCharC() throws IOException
	{
		readSize = 1;
		d.readFully(w, 0, 1);
		if (w[0] < 0) {
			return w[0] + 256;
		}
		return (w[0] & 0xff);
	}

	// note: returns an int, even though says Byte.
	public final int readUnsignedByte() throws IOException
	{
		readSize = 1;
		d.readFully(w, 0, 1);
		if (w[0] < 0)
			throw new IllegalArgumentException();
		return w[0];
	}

	@SuppressWarnings("deprecation")
	public final String readLine() throws IOException
	{
		return d.readLine();
	}

	public final String readUTF() throws IOException
	{
		return d.readUTF();
	}

	public final String readUTF(int size, String encoding, List<Byte> orgList) throws IOException
	{
		byte[] rc = new byte[size];
		d.readFully(rc, 0, size);
		for (int i = 0; i < size; i++) {
			orgList.add(rc[i]);
		}
		if (encoding != null) {
			return new String(rc, encoding);
		} else {
			return new String(rc);
		}
	}

	public final static String readUTF(DataInput in) throws IOException
	{
		return DataInputStream.readUTF(in);
	}

	public final void close() throws IOException
	{
		d.close();
	}

	public final void readShortArray(short[] datas, List<Byte> orgList) throws IOException
	{

		int size = datas.length;
		for (int i = 0; i < size; i++) {
			datas[i] = readShort();
			readOrgList(orgList);
		}
	}

	public final void readUShortArray(int[] datas, List<Byte> orgList) throws IOException
	{

		int size = datas.length;
		for (int i = 0; i < size; i++) {
			datas[i] = readUnsignedShort();
			readOrgList(orgList);
		}
	}

}
