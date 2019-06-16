package idea_exam;

/**
 * ˼·����ʵ��DES�Ļ����Ͻ��жԱȣ��������޸ģ��޸�ΪIDEA
 * @author dell
 *
 */
public class IDEA {
	private byte[] Encrypt(byte[] bytekey, byte[] inputBytes, boolean flag) {// ÿһ�ּ��ܺ���
		byte[] encryptCode = new byte[8];
		/*
		 * ��ȡ����Կ
		 */
		int[] key = get_subkey(flag, bytekey);// �ֽ�����Կ
		/*
		 * ���м��ܲ���
		 */
		encrypt(key, inputBytes, encryptCode);// ���м��ܲ���
		return encryptCode;// ���ؼ�������
	}

	private int bytesToInt(byte[] inBytes, int startPos) {// ����������ת��Ϊ�ֽ�
		return ((inBytes[startPos] << 8) & 0xff00) + (inBytes[startPos + 1] & 0xff);
	}

	private void intToBytes(int inputInt, byte[] outBytes, int startPos) {// �ֽ�ת��Ϊ����������
		outBytes[startPos] = (byte) (inputInt >>> 8);
		outBytes[startPos + 1] = (byte) inputInt;
	}

	private int x_multiply_y(int x, int y) {// �˷�����
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

	private void encrypt(int[] key, byte[] inbytes, byte[] outbytes) {// �Գ��㷨
																		// ���ӽ�����һ����������
		int k = 0;
		int a = bytesToInt(inbytes, 0);// ��64λ���ķ�Ϊ�ĸ��ӿ�
		int b = bytesToInt(inbytes, 2);
		int c = bytesToInt(inbytes, 4);
		int d = bytesToInt(inbytes, 6);
		for (int i = 0; i < 8; i++) { // ����ѭ����ʼ
			a = x_multiply_y(a, key[k++]); // ���裨1��
			b += key[k++]; // ��2��
			b &= 0xffff;
			c += key[k++]; // ��3��
			c &= 0xffff;
			d = x_multiply_y(d, key[k++]); // ��4��
			int tmp1 = b;
			int tmp2 = c;
			c ^= a; // ��5��
			b ^= d; // ��6��
			c = x_multiply_y(c, key[k++]);// ��7��
			b += c; // ��8��
			b &= 0xffff;
			b = x_multiply_y(b, key[k++]);// ��9��
			c += b; // ��10��
			c &= 0xffff;
			a ^= b; // ��11��
			d ^= c; // ��12��
			b ^= tmp2; // ��13��
			c ^= tmp1; // ��14��
		}
		intToBytes(x_multiply_y(a, key[k++]), outbytes, 0); // ��һ��ѭ������ӿ�ת��Ϊ������������һ��ʹ��
		intToBytes(c + key[k++], outbytes, 2);
		intToBytes(b + key[k++], outbytes, 4);
		intToBytes(x_multiply_y(d, key[k]), outbytes, 6);
	}

	private int[] encrypt_subkey(byte[] byteKey) {// ����ʱ����Կ��������
		int[] key = new int[52];
		if (byteKey.length < 16) {
			byte[] tmpkey = new byte[16];
			System.arraycopy(byteKey, 0, tmpkey, tmpkey.length - byteKey.length, byteKey.length);
			byteKey = tmpkey;
		}
		for (int i = 0; i < 8; i++) {
			key[i] = bytesToInt(byteKey, i * 2);
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

	private int fun_a(int a) {// ��������Կ�������㷨
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

	private int fun_b(int b) {// ��������Կ�������㷨
		return (0 - b) & 0xffff;
	}

	private int[] uncrypt_subkey(int[] key) {// ��������Կ��������
		int dec = 52;
		int asc = 0;
		int[] unkey = new int[52];
		int aa = fun_a(key[asc++]);
		int bb = fun_b(key[asc++]);
		int cc = fun_b(key[asc++]);
		int dd = fun_a(key[asc++]);
		unkey[--dec] = dd;
		unkey[--dec] = cc;
		unkey[--dec] = bb;
		unkey[--dec] = aa;
		for (int k1 = 1; k1 < 8; k1++) {
			aa = key[asc++];
			bb = key[asc++];
			unkey[--dec] = bb;
			unkey[--dec] = aa;
			aa = fun_a(key[asc++]);
			bb = fun_b(key[asc++]);
			cc = fun_b(key[asc++]);
			dd = fun_a(key[asc++]);
			unkey[--dec] = dd;
			unkey[--dec] = bb;
			unkey[--dec] = cc;
			unkey[--dec] = aa;
		}
		aa = key[asc++];
		bb = key[asc++];
		unkey[--dec] = bb;
		unkey[--dec] = aa;
		aa = fun_a(key[asc++]);
		bb = fun_b(key[asc++]);
		cc = fun_b(key[asc++]);
		dd = fun_a(key[asc]);
		unkey[--dec] = dd;
		unkey[--dec] = cc;
		unkey[--dec] = bb;
		unkey[--dec] = aa;
		return unkey;
	}

	private int[] get_subkey(boolean flag, byte[] bytekey) {// ��ȡ�ӻ��������Կ��flag�ж���־
		if (flag) {
			/*
			 * flag ==true ���ܲ���
			 */
			return encrypt_subkey(bytekey);
		} else {
			/*
			 * flag==false ���ܲ���
			 */
			return uncrypt_subkey(encrypt_subkey(bytekey));
		}
	}

	private byte[] ByteDataFormat(byte[] data, int unit) { // �ַ�������ƴ�Ӻ���
		int len = data.length;
		int padlen = unit - (len % unit);
		int newlen = len + padlen;
		byte[] newdata = new byte[newlen];
		System.arraycopy(data, 0, newdata, 0, len);
		for (int i = len; i < newlen; i++)
			newdata[i] = (byte) padlen;
		return newdata;
	}

	public byte[] IdeaEncrypt(byte[] idea_key, byte[] idea_data, boolean flag) {// ����ӽ��ܺ�������flag����
		byte[] format_key = ByteDataFormat(idea_key, 16);
		byte[] format_data = ByteDataFormat(idea_data, 8);
		int datalen = format_data.length;
		int unitcount = datalen / 8;
		byte[] result_data = new byte[datalen];
		for (int i = 0; i < unitcount; i++) {
			byte[] tmpkey = new byte[16];
			byte[] tmpdata = new byte[8];
			System.arraycopy(format_key, 0, tmpkey, 0, 16);
			System.arraycopy(format_data, i * 8, tmpdata, 0, 8);
			byte[] tmpresult = Encrypt(tmpkey, tmpdata, flag);
			System.arraycopy(tmpresult, 0, result_data, i * 8, 8);
		}
		return result_data;
	}

	public static void main(String[] args) {
		/*
		 * �������Կ��Ϣ
		 */
		String key = "0000000000000000";
//		String data = "11111111";
		/*
		 * ��Ҫ���ܵ���Ϣ
		 */
		String data = "abcdABCD123";

		/*
		 * ��Կ�Լ�����ת��Ϊbyte��ʽ
		 */
		byte[] bytekey = key.getBytes();
		byte[] bytedata = data.getBytes();
		IDEA idea = new IDEA();
		/*
		 * ������Ϣ   true/false�����ж�
		 * ���ֽڽ��м��ܵĲ�����
		 * true ������
		 */
		byte[] encryptdata = idea.IdeaEncrypt(bytekey, bytedata, true);
		StringBuilder sb=new StringBuilder();
		/*
		 * ������Ϣ
		 * false ������
		 */
//		System.out.println(new String(encryptdata));
		String s=new String(encryptdata);
//		System.out.println(s);
		byte[] by=s.getBytes();
		/*
		 * �˴�ʵ�ֵļ�������ܲ��ܽ���ֱ�ӵ�String���͵�ת����������Ҫʹ���ַ���ʽ���в���
		 */
		byte[] decryptdata = idea.IdeaEncrypt(bytekey, by, false);
		System.out.println("--------------------------------");
		System.out.print("����");
		for (int i = 0; i < bytedata.length; i++) {
			char ss=(char)bytedata[i];
			System.out.print(" " + ss + " ");
		}
		System.out.println();
		System.out.println("-----------------------------------");
		System.out.print("����");
		for (int i = 0; i < encryptdata.length; i++) {
			char ss=(char)encryptdata[i];
			sb.append(ss);
			System.out.print(" " + ss + " ");
		}
		
		System.out.println();
		System.out.println("-------------------------------");
		System.out.println(sb.toString());
		System.out.print("����");
		
		
		for (int i = 0; i < bytedata.length; i++) {
			char ss=(char)decryptdata[i];
			System.out.print(" " + ss + " ");
		}
		System.out.println("");
		System.out.println("--------------------------------");
	}
	
}

// ���н����
//
// C:\test>java IDEA
// -------------------------------------------------------------------------
// 49 49 49 49 49 49 49 49
// ----------------------------------------------------------------------------------
// 72 -105 45 2 -44 -122 122 21 32 9 89 -75 19 28 -62 88
// ------------------------------------------------------------------------------------------------
// 49 49 49 49 49 49 49 49 8 8 8 8 8 8 8 8 -83 -31 -57 -13 24 -31 -113 25
// ----------------------------------------------------------------------------------------------------------
