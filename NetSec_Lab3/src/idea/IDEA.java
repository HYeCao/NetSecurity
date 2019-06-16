package idea;

public class IDEA {
	/**
	 * 初始化加密操作，加密解密由flag控制
	 * @param key
	 * @param data
	 * @param flag
	 * @return
	 */
	public static byte[] IDEAEncrypt(byte[] key, byte[] data, boolean flag) {// 具体加解密函数，由flag控制
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
	 * byte格式的数据拼接(可能问题解决得关键点)
	 * @param data
	 * @param unit
	 * @return
	 */
	private static byte[] ByteDataFormat(byte[] data, int unit) { // 字符串数组拼接函数
		int len = data.length;
		int padlen = unit - (len % unit);
		int newlen = len + padlen;
		byte[] newdata = new byte[newlen];
		System.arraycopy(data, 0, newdata, 0, len);//数组的复制
		for (int i = len; i < newlen; i++)
			newdata[i] = (byte) padlen;
		return newdata;
	}

	/**
	 * 具体的加密流程函数
	 * @param bytekey
	 * @param inputBytes
	 * @param flag
	 * @return
	 */
	private static byte[] Encrypt(byte[] bytekey, byte[] inputBytes, boolean flag) {// 每一轮加密函数
		byte[] encryptCode = new byte[8];
		/*
		 * 获取子密钥
		 */
		int[] key = GetSubKey(flag, bytekey);// 分解子密钥
		/*
		 * 进行加密操作
		 */
		encrypt(key, inputBytes, encryptCode);// 进行加密操作
		return encryptCode;// 返回加密数据
	}

	/**
	 * 获取每一轮解密过程的子密钥
	 * @param flag
	 * @param bytekey
	 * @return
	 */
	private static int[] GetSubKey(boolean flag, byte[] bytekey) {// 获取加或解密子密钥，flag判定标志
		if (flag) {
			/*
			 * flag ==true 加密操作
			 */
			return EnSubKey(bytekey);
		} else {
			/*
			 * flag==false 解密操作
			 */
			return DeSubKey(EnSubKey(bytekey));
		}
	}

	/**
	 * 每一轮 结合子密钥，进行具体的数据加密操作
	 * @param key
	 * @param inbytes
	 * @param outbytes
	 */
	private static void encrypt(int[] key, byte[] inbytes, byte[] outbytes) {// 对称算法
		// ，加解密用一个函数操作
		int k = 0;
//		int a = bytesToInt(inbytes, 0);// 将64位明文分为四个子块
		int  a=((inbytes[0] << 8) & 0xff00) + (inbytes[0 + 1] & 0xff);
//		int b = bytesToInt(inbytes, 2);
		int b=((inbytes[2] << 8) & 0xff00) + (inbytes[2 + 1] & 0xff);
//		int c = bytesToInt(inbytes, 4);
		int c=((inbytes[4] << 8) & 0xff00) + (inbytes[4 + 1] & 0xff);
//		int d = bytesToInt(inbytes, 6);
		int d =((inbytes[6] << 8) & 0xff00) + (inbytes[6 + 1] & 0xff);
		/*
		 * 八轮循环的实现
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
		intToBytes(Mul(a, key[k++]), outbytes, 0); // 将一轮循环后的子块转化为二进制数组下一轮使用
		intToBytes(c + key[k++], outbytes, 2);
		intToBytes(b + key[k++], outbytes, 4);
		intToBytes(Mul(d, key[k]), outbytes, 6);
	}

	/**
	 * 加密子密钥的过程
	 * @param byteKey
	 * @return
	 */
	private static int[] EnSubKey(byte[] byteKey) {// 加密时子密钥产生过程
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
	 * byte数据转化为int数据
	 * @param inBytes
	 * @param startPos
	 * @return
	 */
//	private int bytesToInt(byte[] inBytes, int startPos) {// 二进制数组转换为字节
//		return ((inBytes[startPos] << 8) & 0xff00) + (inBytes[startPos + 1] & 0xff);
//	}

	/**
	 * int数据转化为byte数据
	 * @param inputInt
	 * @param outBytes
	 * @param startPos
	 */
	private static void intToBytes(int inputInt, byte[] outBytes, int startPos) {// 字节转换为二进制数组
		outBytes[startPos] = (byte) (inputInt >>> 8);
		outBytes[startPos + 1] = (byte) inputInt;
	}

	/**
	 * 相乘的运算
	 * @param x
	 * @param y
	 * @return
	 */
	private static int Mul(int x, int y) {// 乘法运算
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
	 * 解密的子密钥的生成过程：平移过程的实现
	 * @param key
	 * @return
	 */
	private static int[] DeSubKey(int[] key) {// 解密子密钥产生过程
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
	 * 解密子密钥的求逆运算（****）
	 * @param a
	 * @return
	 */
	private static int Inverse(int a) {// 解密子密钥中求逆算法
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
