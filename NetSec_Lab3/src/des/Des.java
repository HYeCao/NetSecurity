package des;


public class Des {
	// Replacement IP tables
		private int[] IP_Table = { 
				58, 50, 42, 34, 26, 18, 10, 2, 
				60, 52, 44, 36, 28, 20, 12, 4, 
				62, 54, 46, 38, 30, 22, 14, 6, 
				64, 56, 48, 40, 32, 24, 16, 8, 
				57, 49, 41, 33, 25, 17, 9, 1, 
				59, 51, 43, 35, 27, 19, 11, 3, 
				61, 53, 45, 37, 29, 21, 13, 5, 
				63, 55, 47, 39, 31, 23, 15, 7 
				};
		// Reverse substitution ip-1 table
		private int[] IPR_Table = { 
				40, 8, 48, 16, 56, 24, 64, 32, 
				39, 7, 47, 15, 55, 23, 63, 31, 
				38, 6, 46, 14, 54, 22, 62, 30, 
				37, 5, 45, 13, 53, 21, 61, 29, 
				36, 4, 44, 12, 52, 20, 60, 28, 
				35, 3, 43, 11, 51, 19, 59, 27, 
				34, 2, 42, 10, 50, 18, 58, 26, 
				33, 1, 41, 9, 49, 17, 57, 25 
				};
		// E位选择表(扩展置换表)
		private int[] E_Table = { 
				32, 1, 2, 3, 4, 5, 4, 5, 
				6, 7, 8, 9, 8, 9, 10, 11, 
				12, 13, 12, 13, 14, 15, 16, 17, 
				16, 17, 18, 19, 20, 21, 20, 21, 
				22, 23, 24, 25, 24, 25, 26, 27, 
				28, 29, 28, 29, 30, 31, 32, 1 
				};
		// P换位表(单纯换位表)
		private int[] P_Table = { 
				16, 7, 20, 21, 29, 12, 28, 17, 
				1, 15, 23, 26, 5, 18, 31, 10, 
				2, 8, 24, 14, 32, 27, 3, 9,
				19, 13, 30, 6, 22, 11, 4, 25 
				};
		// PC1选位表(Key generation replacement table1)
		private int[] PC1_Table = { 
				57, 49, 41, 33, 25, 17, 9, 1, 
				58, 50, 42, 34, 26, 18, 10, 2, 
				59, 51, 43, 35, 27, 19, 11, 3, 
				60, 52, 44, 36, 63, 55, 47, 39, 
				31, 23, 15, 7, 62, 54, 46, 38, 
				30, 22, 14, 6, 61, 53, 45, 37, 
				29, 21, 13, 5, 28, 20, 12, 4 
				};
		// PC2选位表(Key generation replacement table2)
		private int[] PC2_Table = { 
				14, 17, 11, 24, 1, 5, 3, 28, 
				15, 6, 21, 10, 23, 19, 12, 4, 
				26, 8, 16, 7, 27, 20, 13, 2,
				41, 52, 31, 37, 47, 55, 30, 40, 
				51, 45, 33, 48, 44, 49, 39, 56, 
				34, 53, 46, 42, 50, 36, 29, 32 
				};
		// Left shift table
		private int[] LOOP_Table = { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 };
		// S box
		private int[][] S_Box = {
				// S1
				{ 14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7,  
					0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8,  
					4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0,  
					15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13 },
				// S2
				{ 15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10,  
					3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5,  
					0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15,  
					13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9 },
				// S3
				{ 10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8,  
					13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1,  
					13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7,  
					1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12 },
				// S4
				{ 7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15,  
					13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9,  
					10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4,  
					3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14 },
				// S5
				{ 2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9,  
					14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6,  
					4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14,  
					11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3 },
				// S6
				{ 12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11,  
					10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8,  
					9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6,  
					4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13 },
				// S7
				{ 4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1,  
					13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6,  
					1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2,  
					6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12 },
				// S8
				{ 13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7,  
					1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2,  
					7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8,  
					2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11 } };

		// 1Subkey generation
		/**
		 * 子密钥的生成函数，结果应用与加密函数之中
		 * @param key
		 * @return 返回16个子密钥构成的数组
		 */
		public StringBuffer[] getSubKey(String key) {
			StringBuffer[] subKey = new StringBuffer[16]; // Store subkey

			// 1.1 Converts the key to a binary string
			/*
			 * 讲初始密钥转化为二进制类型
			 */
			StringBuffer keyBinary = new StringBuffer(); // Store the converted binary key
			for (int i = 0; i < 8; ++i) {
				StringBuffer mSubKeyTemp = new StringBuffer(Integer.toBinaryString(key.charAt(i)));
				while (mSubKeyTemp.length() < 8) {
					mSubKeyTemp.insert(0, 0);
				}
				keyBinary.append(mSubKeyTemp);
			}

			// 1.2 Swap the key with PC1
			/*
			 * 根据PCI表进行密钥的第一次排列，并缩减为56bit
			 */
			StringBuffer substituteKey = new StringBuffer(); // Store the PC1 replacement key
			for (int i = 0; i < 56; ++i) {
				substituteKey.append(keyBinary.charAt(PC1_Table[i] - 1));
			}

			// 1.3 Divide into left and right C0 and D0
			/*
			 * 密钥分割为两部分，各28bit
			 */
			StringBuffer C0 = new StringBuffer(); // Store the left block of the key
			StringBuffer D0 = new StringBuffer(); // Store the right block of the key
			C0.append(substituteKey.substring(0, 28));
			D0.append(substituteKey.substring(28));

			// 1.4 Loop 16 times to generate the subkey
			/*
			 * 16次循环创建子密钥，每次循环保存一个子密钥，最终获取16个子密钥
			 */
			for (int i = 0; i < 16; ++i) {
				// Move left according to the LOOP Table
				/*
				 * 两端密钥均进行左移操作 Each half circularly shifted left by one bit (rounds 1,2,9 and 16) or 2 bits (all other rounds)
				 * 此处实现左移的方式：按LOOP_Table表中的数据实现（ 1 还是 2 ）
				 */
				for (int j = 0; j < LOOP_Table[i]; ++j) {
					char mTemp;
					mTemp = C0.charAt(0);
					C0.deleteCharAt(0);
					C0.append(mTemp);
					mTemp = D0.charAt(0);
					D0.deleteCharAt(0);
					D0.append(mTemp);
				}

				// Combine the left and right 
				/*
				 * 左边部分与右边部分相结合
				 */
				StringBuffer C0D0 = new StringBuffer(C0.toString() + D0.toString());

				// According to PC2, C0D0 is compressed to obtain the sub-key
				/*
				 * 结合PC2表进行压缩，56bit-->48bit
				 */
				StringBuffer C0D0Temp = new StringBuffer();
				for (int j = 0; j < 48; ++j) {
					C0D0Temp.append(C0D0.charAt(PC2_Table[j] - 1));
				}

				// Store the child key in an array
				subKey[i] = C0D0Temp;//每一轮的子密钥保存至数组之中
			}
			return subKey;
		}

		
		//Format the ciphertext
		/**
		 * Same algorithm and key as encryption
		 * Subkeys are applied in opposite order 
		 *  Subkey 16 used in first round
		 *  Subkey 15 used in second round
		 *  Subkey 1 used in 16th round
		 * @param plaintext
		 * @param key
		 * @param type
		 * @return
		 */
		public String encrypt(String plaintext, String key, String type) {

			if(plaintext == null||plaintext.equals("")) {
				plaintext = "*";
			}
			
			int len = plaintext.length();
			StringBuilder pText = new StringBuilder(plaintext);
			StringBuilder result = new StringBuilder();
			int zeroizeNum = 8 - len % 8;

			if(zeroizeNum != 8) {
				for (int i = 0; i < zeroizeNum; i++) {
					pText.append("*");
				}
			}
			/*
			 * 讲输入的文本修正为64bit的倍数
			 * 1 个字符占8bit  因此8个字符占64bit
			 */
			plaintext = pText.toString();
			/*
			 * 分割字符后的段数（每段64bit，8个字符）
			 */
			int splitPTextSize = pText.length() / 8;
			String[] splitPText = new String[splitPTextSize];
			/*
			 * 按段数进行分割，按分割段进行加密（因为每个分割段是64bit）
			 */
			for (int i = 0; i < splitPTextSize; i++) {
				splitPText[i] = plaintext.substring(0, 8);
				plaintext = plaintext.substring(8);
			}
			/*
			 * 按每个分割段进行加密操作，具体实现在encryptFun函数之中，结果均保存在result之中
			 */
			for (String string : splitPText) {
				result.append(encryptFun(string, key, type));
			}

			return result.toString();

		}

		// 2 type(decrypt or encrypt ) key(Symmetrical secret key)
		/**
		 * 具体的加密函数，每次加密64bit数据（8个字符，因此字符需要向字节进行转化）
		 * @param plaintext 明文/密文
		 * @param key  密钥
		 * @param type 用于判断加密还是解密操作
		 * @return
		 */
		private String encryptFun(String plaintext, String key, String type) {

			StringBuffer ciphertext = new StringBuffer(); // Storing cryptograph

			// 2.1 Converts plaintext to a binary string
			StringBuffer plaintextBinary = new StringBuffer(); // Store plaintext binary


			for (int i = 0; i < 8; ++i) {
				/*
				 * string文本转化为字节类型
				 * toBinaryString：十进制转换为二进制输出
				 */
				StringBuffer mSubPlaintextTemp = new StringBuffer(Integer.toBinaryString(plaintext.charAt(i)));
				while (mSubPlaintextTemp.length() < 8) {
					mSubPlaintextTemp.insert(0, 0);
				}
				plaintextBinary.append(mSubPlaintextTemp);
			}

			// 2.2 Replace plaintext with IP
			/*
			 * 针对初始的bit进行排序
			 */
			StringBuffer substitutePlaintext = new StringBuffer(); // Store the plaintext after the replacement
			for (int i = 0; i < 64; ++i) {
				substitutePlaintext.append(plaintextBinary.charAt(IP_Table[i] - 1));
			}

			// 2.3 The plaintext after the substitution is divided into two left and right
			StringBuffer L = new StringBuffer(substitutePlaintext.substring(0, 32));
			StringBuffer R = new StringBuffer(substitutePlaintext.substring(32));

			// 2.4 Get the subkey
			/*
			 * 获取子密钥进行XOR操作
			 */
			StringBuffer[] subKey = getSubKey(key);
			/*
			 * 如果是解密，则只需要把加密的子密钥按相反的顺序进行输入执行操作即可
			 */
			if (type.equals("decrypt")) {
				StringBuffer[] mTemp = getSubKey(key);
				for (int i = 0; i < 16; ++i) {
					subKey[i] = mTemp[15 - i];
				}
			}

			// 2.5 16 iterations
			/**
			 * 16次循环迭代的执行
			 */
			for (int i = 0; i < 16; ++i) {
				StringBuffer mLTemp = new StringBuffer(L); // Store the original left side

				// Operation on the left
				L.replace(0, 32, R.toString());

				// Expand the right side of the e-bit selection table
				/*
				 * 1. 32bit --> 48bit
				 */
				StringBuffer mRTemp = new StringBuffer(); // Storage expands to the right
				for (int j = 0; j < 48; ++j) {
					/*
					 * 结合expansion表进行扩充
					 */
					mRTemp.append(R.charAt(E_Table[j] - 1));
				}

				// The expanded right and subkeys differ or differ
				/*
				 * 2. 结合subkeys执行XOR操作
				 */
				for (int j = 0; j < 48; ++j) {
					if (mRTemp.charAt(j) == subKey[i].charAt(j)) {
						mRTemp.replace(j, j + 1, "0");
					} else {
						mRTemp.replace(j, j + 1, "1");
					}
				}

				//  S box compression
				/*
				 * 3.S box执行的操作
				 */
				R.setLength(0);
				for (int j = 0; j < 8; ++j) {
					String mSNumber = mRTemp.substring(j * 6, (j + 1) * 6);
					int row = Integer.parseInt(Character.toString(mSNumber.charAt(0)) + mSNumber.charAt(5), 2);
					int column = Integer.parseInt(mSNumber.substring(1, 5), 2);
					int number = S_Box[j][row * 16 + column];
					StringBuffer numberString = new StringBuffer(Integer.toBinaryString(number));
					while (numberString.length() < 4) {
						numberString.insert(0, 0);
					}
					R.append(numberString);
				}

				//  The compressed R is replaced by P Table
				/*
				 * 4. P - box执行的操作
				 */
				StringBuffer mRTemp1 = new StringBuffer(); // Store the replaced R
				for (int j = 0; j < 32; ++j) {
					mRTemp1.append(R.charAt(P_Table[j] - 1));
				}
				R.replace(0, 32, mRTemp1.toString());

				// The R after the substitution is different or different from the original L
				/*
				 * 5.左右两32bit部分执行的XOR操作
				 */
				for (int j = 0; j < 32; ++j) {
					if (R.charAt(j) == mLTemp.charAt(j)) {
						R.replace(j, j + 1, "0");
					} else {
						R.replace(j, j + 1, "1");
					}
				}
			}

			// 2.6 Merge the iterated L and R
			/*
			 * 左右经过16次循环迭代加密后的两部分进行整合
			 */
			StringBuffer LR = new StringBuffer(R.toString() + L.toString());

			// 2.7 Replace LR with IPR Table
			/*
			 * 再结合初始排列后的规则进行操作，获取原始的排列
			 */
			StringBuffer mLRTemp = new StringBuffer(); // Store LR after replacement
			for (int i = 0; i < 64; ++i) {
				mLRTemp.append(LR.charAt(IPR_Table[i] - 1));
			}

			// 2.8 Convert binary to string
			/*
			 * 讲二进制的数据转化为string类型的数据
			 */
			for (int i = 0; i < 8; ++i) {
				String mCharTemp = mLRTemp.substring(i * 8, (i + 1) * 8);
				ciphertext.append((char) Integer.parseInt(mCharTemp, 2));
			}
			return ciphertext.toString();//返回加密/解密后的信息
		}
}