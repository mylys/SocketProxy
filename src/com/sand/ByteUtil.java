package com.sand;

import java.io.ByteArrayOutputStream;

public class ByteUtil {
	public ByteUtil()
	{
	}

	public static byte[] union(byte bs1[], byte bs2[])
	{
		byte bs[] = new byte[bs1.length + bs2.length];
		for (int i = 0; i < bs1.length; i++)
			bs[i] = bs1[i];

		for (int i = 0; i < bs2.length; i++)
			bs[bs1.length + i] = bs2[i];

		return bs;
	}

	public static byte[] sub(byte bs[], int first, int length)
	{
		length = first + length <= bs.length ? length : bs.length - first;
		byte nb[] = new byte[length];
		for (int i = 0; i < length; i++)
			nb[i] = bs[i + first];

		return nb;
	}

	public static byte lrc(byte buffer[], int count)
	{
		byte lrc = 0;
		int len = buffer.length;
		if (len < count)
			return lrc;
		for (int i = 0; i < count; i++)
			lrc ^= buffer[i];

		return lrc;
	}

	public static byte[] fillByte(byte bs[], byte fillByte, String fillSide, int size)
	{
		if (bs.length > size)
			System.err.println((new StringBuilder("������byte���鳤��")).append(bs.length).append("����������ܳ���").append(size).toString());
		int n = size - bs.length;
		byte tb[] = {
			fillByte
		};
		if ("left".equalsIgnoreCase(fillSide))
		{
			for (int i = 0; i < n; i++)
				bs = union(tb, bs);

		} else
		if ("right".equalsIgnoreCase(fillSide))
		{
			for (int i = 0; i < n; i++)
				bs = union(bs, tb);

		} else
		if ("both".equalsIgnoreCase(fillSide))
		{
			for (int i = 0; i < n; i++)
				if (i % 2 == 0)
					bs = union(tb, bs);
				else
					bs = union(bs, tb);

		}
		return bs;
	}

	public static byte[] fillFixByte(byte bs[], byte fillByte, String fillType, int fillLen)
	{
		int n = fillLen;
		byte tb[] = {
			fillByte
		};
		if ("left".equalsIgnoreCase(fillType))
		{
			for (int i = 0; i < n; i++)
				bs = union(tb, bs);

		} else
		if ("right".equalsIgnoreCase(fillType))
		{
			for (int i = 0; i < n; i++)
				bs = union(bs, tb);

		} else
		if ("both".equalsIgnoreCase(fillType))
		{
			for (int i = 0; i < n; i++)
				if (i % 2 == 0)
					bs = union(tb, bs);
				else
					bs = union(bs, tb);

		}
		return bs;
	}

	public static byte[] trimByte(byte bs[], byte trimByte, String trimSide)
	{
		if ("left".equalsIgnoreCase(trimSide) || "both".equalsIgnoreCase(trimSide))
		{
			int i;
			for (i = 0; i < bs.length && bs[i] == trimByte; i++);
			bs = sub(bs, i, bs.length - i);
		}
		if ("right".equalsIgnoreCase(trimSide) || "both".equalsIgnoreCase(trimSide))
		{
			int i;
			for (i = bs.length - 1; i >= 0 && bs[i] == trimByte; i--);
			bs = sub(bs, 0, i + 1);
		}
		return bs;
	}

	public static byte[] ascii2bcd(byte bs[])
	{
		byte res[] = new byte[bs.length / 2];
		int i = 0;
		for (int n = bs.length; i < n; i += 2)
			res[i / 2] = (byte)(bs[i] << 4 | bs[i + 1] & 0xf);

		return res;
	}

	public static byte[] bcd2ascii(byte bs[])
	{
		byte res[] = new byte[bs.length * 2];
		int i = 0;
		for (int n = bs.length; i < n; i++)
		{
			res[i * 2] = (byte)((bs[i] & 0xf0) >> 4 | 0x30);
			res[i * 2 + 1] = (byte)(bs[i] & 0xf | 0x30);
		}

		return res;
	}

	public static byte[] ascii2hex(byte bs[])
	{
		byte res[] = new byte[bs.length / 2];
		int i = 0;
		for (int n = bs.length; i < n; i += 2)
			res[i / 2] = (byte)Integer.parseInt(new String(bs, i, 2), 16);

		return res;
	}

	public static byte[] hex2ascii(byte bs[])
	{
		byte res[] = new byte[bs.length * 2];
		for (int i = 0; i < bs.length; i++)
		{
			int ti = bs[i];
			ti = ti >= 0 ? ti : ti + 256;
			String t = Integer.toHexString(ti);
			if (t.length() < 2)
				t = (new StringBuilder("0")).append(t).toString();
			res[i * 2] = (byte)t.charAt(0);
			res[i * 2 + 1] = (byte)t.charAt(1);
		}

		return res;
	}

	public static byte[] mackBitMap(boolean source[])
	{
		int n = source.length / 8;
		byte bs[] = new byte[n];
		for (int i = 0; i < n; i++)
		{
			char c = '\0';
			for (int j = 0; j < 8; j++)
				c = source[8 * i + j] ? (char)(c | 1 << 7 - j) : c;

			bs[i] = (byte)c;
		}

		return bs;
	}

	public static boolean[] splitBitMap(byte bs[])
	{
		boolean res[] = new boolean[bs.length * 8];
		for (int i = 0; i < bs.length; i++)
		{
			int x = bs[i] >= 0 ? ((int) (bs[i])) : bs[i] + 256;
			for (int j = 0; j < 8; j++)
				res[i * 8 + j] = (x & 1 << 7 - j) == 1 << 7 - j;

		}

		return res;
	}

	public static String bytes2hex(byte bs[])
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bs.length; i++)
		{
			int nn = bs[i] >= 0 ? ((int) (bs[i])) : bs[i] + 256;
			String t = Integer.toHexString(nn).toUpperCase();
			sb.append(t.length() >= 2 ? t : (new StringBuilder("0")).append(t).toString());
		}

		return sb.toString();
	}

	public static String byte2HexDecimal(byte bs[])
	{
		StringBuffer sb = new StringBuffer();
		String bb = new String(bs);
		for (int i = 0; i < bb.length(); i++)
		{
			int n = Integer.parseInt(bb.substring(i, i + 1));
			String t = Integer.toString(n, 16).toUpperCase();
			sb.append(t.length() >= 2 ? t : (new StringBuilder("0")).append(t).toString());
		}

		return sb.toString();
	}

	public static String intNum2HexStr(int intNum)
	{
		return Integer.toString(intNum, 16);
	}

	public static String hexNum2IntStr(String hexNum)
	{
		return Integer.valueOf(hexNum, 16).toString();
	}

	public static String ascii2str(String ascii)
	{
		StringBuffer sb = new StringBuffer();
		if (ascii.length() % 2 != 0)
			System.err.println("ת����ascii���ַ��Ȳ���2�ı���");
		for (int i = 0; i < ascii.length(); i += 2)
		{
			String s = ascii.substring(i, i + 2);
			sb.append((new StringBuilder()).append((char)Integer.parseInt(hexNum2IntStr(s))).toString());
		}

		return sb.toString();
	}

	public static String toHexInfo(byte bs[])
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bs.length; i++)
		{
			int nn = bs[i] >= 0 ? ((int) (bs[i])) : bs[i] + 256;
			String t = Integer.toHexString(nn).toUpperCase();
			sb.append(t.length() >= 2 ? t : (new StringBuilder("0")).append(t).toString()).append(i >= bs.length - 1 ? "" : " ");
		}

		return sb.toString();
	}

	public static String toAsciiInfo(byte bs[])
	{
		return new String(bs);
	}

	public static byte[] str2bcd(String s)
	{
		if (s.length() % 2 != 0)
			s = (new StringBuilder("0")).append(s).toString();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		char cs[] = s.toCharArray();
		for (int i = 0; i < cs.length; i += 2)
		{
			int high = cs[i] - 48;
			int low = cs[i + 1] - 48;
			baos.write(high << 4 | low);
		}

		return baos.toByteArray();
	}

	public static String bcd2str(byte b[])
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
		{
			int h = ((b[i] & 0xff) >> 4) + 48;
			sb.append((char)h);
			int l = (b[i] & 0xf) + 48;
			sb.append((char)l);
		}

		return sb.toString();
	}

	public static void main(String args1[])
	{
	}
}
