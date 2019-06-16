package idea;

public class IDEA {
	/**
	 * ��ʼ�����ܲ��������ܽ�����flag����
	 * @param key
	 * @param data
	 * @param flag
	 * @return
	 */
	public static byte[] IDEAEncrypt(byte[] key, byte[] data, boolean flag) {// ����ӽ��ܺ�������flag����
		byte[] format_key = ByteDataFormat(key, 16);
		byte[] format_data = ByteDataFormat(data, 8);
		int len = format_data.length;
		int group = len / 8;
		byte[] result_data = new byte[len];
		for (int i = 0; i < group; i++) {
			byte[] tmpkey = new byte[16];
			byte[] tmpdata = new byte[8];
			System.arraycopy(format_key, 0, tmpkey, 0, 16);
			System.arraycopy(format_data, i * 8, tmpdata, 0, 8);
			byte[] tmpresult = Encrypt(tmpkey, tmpdata, flag);
			System.arraycopy(tmpresult, 0, result_data, i * 8, 8);
		}
		return result_data;
	}

	/**
	 * byte��ʽ������ƴ��(�����������ùؼ���)
	 * @param data
	 * @param unit
	 * @return
	 */
	private static byte[] ByteDataFormat(byte[] data, int unit) { // �ַ�������ƴ�Ӻ���
		int len = data.length;
		int padlen = unit - (len % unit);
		int newlen = len + padlen;
		byte[] newdata = new byte[newlen];
		System.arraycopy(data, 0, newdata, 0, len);//����ĸ���
		for (int i = len; i < newlen; i++)
			newdata[i] = (byte) padlen;
		return newdata;
	}

	/**
	 * ����ļ������̺���
	 * @param bytekey
	 * @param inputBytes
	 * @param flag
	 * @return
	 */
	private static byte[] Encrypt(byte[] bytekey, byte[] inputBytes, boolean flag) {// ÿһ�ּ��ܺ���
		byte[] encryptCode = new byte[8];
		/*
		 * ��ȡ����Կ
		 */
		int[] key = GetSubKey(flag, bytekey);// �ֽ�����Կ
		/*
		 * ���м��ܲ���
		 */
		encrypt(key, inputBytes, encryptCode);// ���м��ܲ���
		return encryptCode;// ���ؼ�������
	}

	/**
	 * ��ȡÿһ�ֽ��ܹ��̵�����Կ
	 * @param flag
	 * @param bytekey
	 * @return
	 */
	private static int[] GetSubKey(boolean flag, byte[] bytekey) {// ��ȡ�ӻ��������Կ��flag�ж���־
		if (flag) {
			/*
			 * flag ==true ���ܲ���
			 */
			return EnSubKey(bytekey);
		} else {
			/*
			 * flag==false ���ܲ���
			 */
			return DeSubKey(EnSubKey(bytekey));
		}
	}

	/**
	 * ÿһ�� �������Կ�����о�������ݼ��ܲ���
	 * @param key
	 * @param inbytes
	 * @param outbytes
	 */
	private static void encrypt(int[] key, byte[] inbytes, byte[] outbytes) {// �Գ��㷨
		// ���ӽ�����һ����������
		int k = 0;
//		int a = bytesToInt(inbytes, 0);// ��64λ���ķ�Ϊ�ĸ��ӿ�
		int  a=((inbytes[0] << 8) & 0xff00) + (inbytes[0 + 1] & 0xff);
//		int b = bytesToInt(inbytes, 2);
		int b=((inbytes[2] << 8) & 0xff00) + (inbytes[2 + 1] & 0xff);
//		int c = bytesToInt(inbytes, 4);
		int c=((inbytes[4] << 8) & 0xff00) + (inbytes[4 + 1] & 0xff);
//		int d = bytesToInt(inbytes, 6);
		int d =((inbytes[6] << 8) & 0xff00) + (inbytes[6 + 1] & 0xff);
		/*
		 * ����ѭ����ʵ��
		 */
		for (int i = 0; i < 8; i++) { 
			/*
			 * 1 P1 * K1
			 */
			a = Mul(a, key[k++]); 
			/*
			 * 2 P2 + K2
			 */
			b += key[k++]; 
			b &= 0xffff;
			/*
			 * 3 P3 + K3
			 */
			c += key[k++]; 
			c &= 0xffff;
			/*
			 * 4 P4 * K4
			 */
			d = Mul(d, key[k++]); 
			int tmp1 = b;
			int tmp2 = c;
			/*
			 * 5 1 XOR 3
			 */
			c ^= a; 
			/*
			 * 6 2 XOR 4
			 */
			b ^= d;
			/*
			 * 7 5 * K5
			 */
			c = Mul(c, key[k++]);
			/*
			 * 8 6+7
			 */
			b += c; 
			b &= 0xffff;
			/*
			 * 9 8 * K6
			 */
			b = Mul(b, key[k++]);
			/*
			 * 10 7+9
			 */
			c += b; 
			c &= 0xffff;
			/*
			 * 11 1 XOR 9 -->R1
			 */
			a ^= b; 
			/*
			 * 12 3 XOR 9 -->R2
			 */
			d ^= c; 
			/*
			 * 13 2 XOR 10 -->R3
			 */
			b ^= tmp2; 
			/*
			 * 14 4 XOR 10 -->R4
			 */
			c ^= tmp1; 
		}
		intToBytes(Mul(a, key[k++]), outbytes, 0); // ��һ��ѭ������ӿ�ת��Ϊ������������һ��ʹ��
		intToBytes(c + key[k++], outbytes, 2);
		intToBytes(b + key[k++], outbytes, 4);
		intToBytes(Mul(d, key[k]), outbytes, 6);
	}

	/**
	 * ��������Կ�Ĺ���
	 * @param byteKey
	 * @return
	 */
	private static int[] EnSubKey(byte[] byteKey) {// ����ʱ����Կ��������
		int[] key = new int[52];
		if (byteKey.length < 16) {
			byte[] tmpkey = new byte[16];
			System.arraycopy(byteKey, 0, tmpkey, tmpkey.length - byteKey.length, byteKey.length);
			byteKey = tmpkey;
		}
		for (int i = 0; i < 8; i++) {
//			key[i] = bytesToInt(byteKey, i * 2);(inBytes[startPos] << 8) & 0xff00) + (inBytes[startPos + 1] & 0xff
			key[i]=((byteKey[i * 2] << 8) & 0xff00) + (byteKey[i * 2 + 1] & 0xff);
		}
		for (int j = 8; j < 52; j++) {
			if ((j & 0x7) < 6) {
				key[j] = (((key[j - 7] & 0x7f) << 9) | (key[j - 6] >> 7)) & 0xffff;
			} else if ((j & 0x7) == 6) {
				key[j] = (((key[j - 7] & 0x7f) << 9) | (key[j - 14] >> 7)) & 0xffff;
			} else {
				key[j] = (((key[j - 15] & 0x7f) << 9) | (key[j - 14] >> 7)) & 0xffff;
			}
		}
		return key;
	}
	/**
	 * byte����ת��Ϊint����
	 * @param inBytes
	 * @param startPos
	 * @return
	 */
//	private int bytesToInt(byte[] inBytes, int startPos) {// ����������ת��Ϊ�ֽ�
//		return ((inBytes[startPos] << 8) & 0xff00) + (inBytes[startPos + 1] & 0xff);
//	}

	/**
	 * int����ת��Ϊbyte����
	 * @param inputInt
	 * @param outBytes
	 * @param startPos
	 */
	private static void intToBytes(int inputInt, byte[] outBytes, int startPos) {// �ֽ�ת��Ϊ����������
		outBytes[startPos] = (byte) (inputInt >>> 8);
		outBytes[startPos + 1] = (byte) inputInt;
	}

	/**
	 * ��˵�����
	 * @param x
	 * @param y
	 * @return
	 */
	private static int Mul(int x, int y) {// �˷�����
		if (x == 0) {
			x = 0x10001 - y;
		} else if (y == 0) {
			x = 0x10001 - x;
		} else {
			int tmp = x * y;
			y = tmp & 0xffff;
			x = tmp >>> 16;
			x = (y - x) + ((y < x) ? 1 : 0);
		}
		return x & 0xffff;
	}
	/**
	 * ���ܵ�����Կ�����ɹ��̣�ƽ�ƹ��̵�ʵ��
	 * @param key
	 * @return
	 */
	private static int[] DeSubKey(int[] key) {// ��������Կ��������
		int dec = 52;
		int asc = 0;
		int[] unkey = new int[52];
		int a = Inverse(key[asc++]);
		int b = (0 - key[asc++]) & 0xffff;
		int c = (0 - key[asc++]) & 0xffff;
		int d = Inverse(key[asc++]);
		unkey[--dec] = d;
		unkey[--dec] = c;
		unkey[--dec] = b;
		unkey[--dec] = a;
		for (int k1 = 1; k1 < 8; k1++) {
			a = key[asc++];
			b = key[asc++];
			unkey[--dec] = b;
			unkey[--dec] = a;
			a = Inverse(key[asc++]);
			b = (0 - key[asc++]) & 0xffff;
			c = (0 - key[asc++]) & 0xffff;
			d = Inverse(key[asc++]);
			unkey[--dec] = d;
			unkey[--dec] = b;
			unkey[--dec] = c;
			unkey[--dec] = a;
		}
		a = key[asc++];
		b = key[asc++];
		unkey[--dec] = b;
		unkey[--dec] = a;
		a = Inverse(key[asc++]) ;
		b = (0 - key[asc++]) & 0xffff;
		c = (0 - key[asc++]) & 0xffff;
		d = Inverse(key[asc]);
		unkey[--dec] = d;
		unkey[--dec] = c;
		unkey[--dec] = b;
		unkey[--dec] = a;
		return unkey;
	}
	/**
	 * ��������Կ���������㣨****��
	 * @param a
	 * @return
	 */
	private static int Inverse(int a) {// ��������Կ�������㷨
		if (a < 2) {
			return a;
		}
		int b = 1;
		int c = 0x10001 / a;
		for (int i = 0x10001 % a; i != 1;) {
			int d = a / i;
			a %= i;
			b = (b + (c * d)) & 0xffff;
			if (a == 1) {
				return b;
			}
			d = i / a;
			i %= a;
			c = (c + (b * d)) & 0xffff;
		}
		return (1 - c) & 0xffff;
	}

}
