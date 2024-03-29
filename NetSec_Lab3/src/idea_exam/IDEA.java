package idea_exam;

/**
 * 思路：在实现DES的基础上进行对比，随后进行修改，修改为IDEA
 * @author dell
 *
 */
public class IDEA {
	private byte[] Encrypt(byte[] bytekey, byte[] inputBytes, boolean flag) {// 每一轮加密函数
		byte[] encryptCode = new byte[8];
		/*
		 * 获取子密钥
		 */
		int[] key = get_subkey(flag, bytekey);// 分解子密钥
		/*
		 * 进行加密操作
		 */
		encrypt(key, inputBytes, encryptCode);// 进行加密操作
		return encryptCode;// 返回加密数据
	}

	private int bytesToInt(byte[] inBytes, int startPos) {// 二进制数组转换为字节
		return ((inBytes[startPos] << 8) & 0xff00) + (inBytes[startPos + 1] & 0xff);
	}

	private void intToBytes(int inputInt, byte[] outBytes, int startPos) {// 字节转换为二进制数组
		outBytes[startPos] = (byte) (inputInt >>> 8);
		outBytes[startPos + 1] = (byte) inputInt;
	}

	private int x_multiply_y(int x, int y) {// 乘法运算
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

	private void encrypt(int[] key, byte[] inbytes, byte[] outbytes) {// 对称算法
																		// ，加解密用一个函数操作
		int k = 0;
		int a = bytesToInt(inbytes, 0);// 将64位明文分为四个子块
		int b = bytesToInt(inbytes, 2);
		int c = bytesToInt(inbytes, 4);
		int d = bytesToInt(inbytes, 6);
		for (int i = 0; i < 8; i++) { // 八轮循环开始
			a = x_multiply_y(a, key[k++]); // 步骤（1）
			b += key[k++]; // （2）
			b &= 0xffff;
			c += key[k++]; // （3）
			c &= 0xffff;
			d = x_multiply_y(d, key[k++]); // （4）
			int tmp1 = b;
			int tmp2 = c;
			c ^= a; // （5）
			b ^= d; // （6）
			c = x_multiply_y(c, key[k++]);// （7）
			b += c; // （8）
			b &= 0xffff;
			b = x_multiply_y(b, key[k++]);// （9）
			c += b; // （10）
			c &= 0xffff;
			a ^= b; // （11）
			d ^= c; // （12）
			b ^= tmp2; // （13）
			c ^= tmp1; // （14）
		}
		intToBytes(x_multiply_y(a, key[k++]), outbytes, 0); // 将一轮循环后的子块转化为二进制数组下一轮使用
		intToBytes(c + key[k++], outbytes, 2);
		intToBytes(b + key[k++], outbytes, 4);
		intToBytes(x_multiply_y(d, key[k]), outbytes, 6);
	}

	private int[] encrypt_subkey(byte[] byteKey) {// 加密时子密钥产生过程
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

	private int fun_a(int a) {// 解密子密钥中求逆算法
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

	private int fun_b(int b) {// 解密子密钥中求逆算法
		return (0 - b) & 0xffff;
	}

	private int[] uncrypt_subkey(int[] key) {// 解密子密钥产生过程
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

	private int[] get_subkey(boolean flag, byte[] bytekey) {// 获取加或解密子密钥，flag判定标志
		if (flag) {
			/*
			 * flag ==true 加密操作
			 */
			return encrypt_subkey(bytekey);
		} else {
			/*
			 * flag==false 解密操作
			 */
			return uncrypt_subkey(encrypt_subkey(bytekey));
		}
	}

	private byte[] ByteDataFormat(byte[] data, int unit) { // 字符串数组拼接函数
		int len = data.length;
		int padlen = unit - (len % unit);
		int newlen = len + padlen;
		byte[] newdata = new byte[newlen];
		System.arraycopy(data, 0, newdata, 0, len);
		for (int i = len; i < newlen; i++)
			newdata[i] = (byte) padlen;
		return newdata;
	}

	public byte[] IdeaEncrypt(byte[] idea_key, byte[] idea_data, boolean flag) {// 具体加解密函数，由flag控制
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
		 * 输入的密钥信息
		 */
		String key = "0000000000000000";
//		String data = "11111111";
		/*
		 * 所要加密的信息
		 */
		String data = "abcdABCD123";

		/*
		 * 密钥以及明文转化为byte形式
		 */
		byte[] bytekey = key.getBytes();
		byte[] bytedata = data.getBytes();
		IDEA idea = new IDEA();
		/*
		 * 加密信息   true/false进行判断
		 * 按字节进行加密的操作？
		 * true ：加密
		 */
		byte[] encryptdata = idea.IdeaEncrypt(bytekey, bytedata, true);
		StringBuilder sb=new StringBuilder();
		/*
		 * 解密信息
		 * false ：解密
		 */
//		System.out.println(new String(encryptdata));
		String s=new String(encryptdata);
//		System.out.println(s);
		byte[] by=s.getBytes();
		/*
		 * 此处实现的加密与解密不能进行直接的String类型的转化，还是需要使用字符形式进行操作
		 */
		byte[] decryptdata = idea.IdeaEncrypt(bytekey, by, false);
		System.out.println("--------------------------------");
		System.out.print("明文");
		for (int i = 0; i < bytedata.length; i++) {
			char ss=(char)bytedata[i];
			System.out.print(" " + ss + " ");
		}
		System.out.println();
		System.out.println("-----------------------------------");
		System.out.print("密文");
		for (int i = 0; i < encryptdata.length; i++) {
			char ss=(char)encryptdata[i];
			sb.append(ss);
			System.out.print(" " + ss + " ");
		}
		
		System.out.println();
		System.out.println("-------------------------------");
		System.out.println(sb.toString());
		System.out.print("明文");
		
		
		for (int i = 0; i < bytedata.length; i++) {
			char ss=(char)decryptdata[i];
			System.out.print(" " + ss + " ");
		}
		System.out.println("");
		System.out.println("--------------------------------");
	}
	
}

// 运行结果：
//
// C:\test>java IDEA
// -------------------------------------------------------------------------
// 49 49 49 49 49 49 49 49
// ----------------------------------------------------------------------------------
// 72 -105 45 2 -44 -122 122 21 32 9 89 -75 19 28 -62 88
// ------------------------------------------------------------------------------------------------
// 49 49 49 49 49 49 49 49 8 8 8 8 8 8 8 8 -83 -31 -57 -13 24 -31 -113 25
// ----------------------------------------------------------------------------------------------------------
