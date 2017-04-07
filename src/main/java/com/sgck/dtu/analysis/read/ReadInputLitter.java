package com.sgck.dtu.analysis.read;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.sgck.dtu.analysis.exception.DtuMessageException;

/**
 * 小端
 * 
 * @author DELL
 *
 */
public class ReadInputLitter implements ReadInput
{

	public final void readFully(InputStream in, byte b[], int off, int len) throws IOException, DtuMessageException
	{
		if (len < 0)
			throw new IndexOutOfBoundsException();
		int n = 0;
		while (n < len) {
			int count = in.read(b, off + n, len - n);
			if (count < 0)
				throw new DtuMessageException(DtuMessageException.CLIENT_NOT_ONLINE);
			n += count;
		}
	}

	public final void readToList(byte[] w, List<Byte> list)
	{
		int i = w.length;
		while (i > 0) {
			list.add(w[i - 1]);
			i--;
		}
	}

	public final short readShort(InputStream in, List<Byte> list) throws IOException, DtuMessageException
	{

		byte[] w = new byte[2];
		readFully(in, w, 0, 2);
		readToList(w, list);
		return (short) ((w[1] & 0xff) << 8 | (w[0] & 0xff));
	}

	public final int readUnsignedShort(InputStream in, List<Byte> list) throws IOException
	{
		byte[] w = new byte[2];
		readFully(in, w, 0, 2);
		readToList(w, list);
		return ((w[1] & 0xff) << 8 | (w[0] & 0xff));
	}

	public final char readChar(InputStream in, List<Byte> list) throws IOException
	{
		byte[] w = new byte[2];
		readFully(in, w, 0, 2);
		readToList(w, list);
		return (char) ((w[1] & 0xff) << 8 | (w[0] & 0xff));
	}

	public final int readInt(InputStream in, List<Byte> list) throws DtuMessageException, IOException
	{
		byte[] w = new byte[4];
		readFully(in, w, 0, 4);
		readToList(w, list);
		return (w[3] & 0xff) << 24 | (w[2] & 0xff) << 16 | (w[1] & 0xff) << 8 | (w[0] & 0xff);
	}

	public final long readUnsignedInt(InputStream in, List<Byte> list) throws IOException
	{
		byte[] w = new byte[4];
		readFully(in, w, 0, 4);
		readToList(w, list);
		return getLong(w, 0);
	}

	public final long readLong(InputStream in, List<Byte> list) throws IOException
	{
		byte[] w = new byte[8];
		readFully(in, w, 0, 8);
		readToList(w, list);
		return (long) (w[7] & 0xff) << 56
				| /* long cast needed or shift done modulo 32 */
				(long) (w[6] & 0xff) << 48 | (long) (w[5] & 0xff) << 40 | (long) (w[4] & 0xff) << 32 | (long) (w[3] & 0xff) << 24 | (long) (w[2] & 0xff) << 16 | (long) (w[1] & 0xff) << 8 | (long) (w[0] & 0xff);
	}

	public final float readFloat(InputStream in, List<Byte> list) throws IOException
	{
		return Float.intBitsToFloat(readInt(in, list));
	}

	/**
	 * like DataInputStream.readDouble except little endian.
	 */
	public final double readDouble(InputStream in, List<Byte> list) throws IOException
	{
		return Double.longBitsToDouble(readLong(in, list));
	}

	public final boolean readBoolean(InputStream in, List<Byte> list) throws IOException
	{
		byte[] w = new byte[1];
		readFully(in, w, 0, 1);
		if (w[0] < 0)
			throw new IllegalArgumentException();
		readToList(w, list);
		return w[0] != 0;
	}

	public final byte readUnsignedByte(InputStream in, List<Byte> list) throws IOException
	{
		byte[] w = new byte[1];
		readFully(in, w, 0, 1);
		if (w[0] < 0)
			throw new IllegalArgumentException();
		readToList(w, list);
		return (byte) w[0];
	}

	public final int readCharC(InputStream in, List<Byte> list) throws IOException
	{
		byte[] w = new byte[1];
		readFully(in, w, 0, 1);
		if (w[0] < 0) {
			readToList(w, list);
			return w[0] + 256;
		}
		readToList(w, list);
		return (w[0] & 0xff);
	}

	// note: returns an int, even though says Byte.
	public final int readByte(InputStream in, List<Byte> list) throws IOException
	{
		byte[] w = new byte[1];
		readFully(in, w, 0, 1);
		readToList(w, list);
		return (byte) w[0];
	}

	public final void readShortArray(InputStream in, short[] datas, List<Byte> list) throws IOException
	{

		int size = datas.length;
		for (int i = 0; i < size; i++) {
			datas[i] = readShort(in, list);
		}
	}

	public final void readUShortArray(InputStream in, int[] datas, List<Byte> list) throws IOException
	{

		int size = datas.length;
		for (int i = 0; i < size; i++) {
			datas[i] = readUnsignedShort(in, list);
		}
	}

	public final String readUTF(InputStream in, int len, List<Byte> list) throws IOException
	{
		byte[] w = new byte[len];
		readFully(in, w, 0, len);
		readToList(w, list);
		return new String(w);
	}

	public final String readUTF(InputStream in, int len, List<Byte> list, String encoding) throws IOException
	{
		byte[] w = new byte[len];
		readFully(in, w, 0, len);
		readToList(w, list);
		return new String(w, encoding);
	}

	public long getLong(byte buf[], int index)
	{
		int firstByte = (0x000000FF & ((int) buf[index]));
		int secondByte = (0x000000FF & ((int) buf[index + 1]));
		int thirdByte = (0x000000FF & ((int) buf[index + 2]));
		int fourthByte = (0x000000FF & ((int) buf[index + 3]));
		long unsignedLong = ((long) (firstByte | secondByte << 8 | thirdByte << 16 | fourthByte << 24)) & 0xFFFFFFFFL;
		return unsignedLong;
	}

	@Override
	public void readUShortArray(InputStream in, int[] datas, List<Byte> list, List<Number> wavePackage) throws DtuMessageException, IOException
	{
		// TODO Auto-generated method stub
		
	}

}
