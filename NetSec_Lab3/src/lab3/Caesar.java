/**
 * 
 */
package lab3;

/**
 * @author dell
 *
 */
/*
 * Solution of the problem: 
 * 1. Read clear text information 
 * 2. Read key information 
 * 3. If the key is a positive number then it is looping, encrypting the plaintext 
 * 4. If the key is negative, it will loop backwards, encrypting the plaintext. 
 * 5. Write of encrypted information
 *
 * Key issues: 1. Loop implementation, ASSII code and char conversion problem 
 *              2.Processing of characters other than letters
 */
public class Caesar {
	static String out = "";// info in
	static StringBuffer buffer;// info read
	// StringBuilder out;//info write
	static int key;// key
	/**
	 * ASCIIÂëÓëcharµÄ×Ö·û¶ÔÕÕ
	 */

	/*
	 * 65--90 A--Z 97--122 a--z
	 */

	/**
	 * Obtaining and preliminary processing of plaintext information and key information
	 * 
	 * @param info
	 * @param ke
	 * @return
	 */
	public static String CaeInfo(String info, int ke) {
		StringBuffer bu = new StringBuffer(info);
		key = ke;
		buffer = bu;
		return deal();
	}

	public static String deal() {
		int len = buffer.length();
		int num;
		char al;
		for (int i = 0; i < len; i++) {
			if (buffer.charAt(i) == ' ' || buffer.charAt(i) == '\n') {
				out = String.valueOf(out) + buffer.charAt(i);
			} else {
				num = buffer.charAt(i);
				num = Reverse(num);
				al = (char) num;
				out = String.valueOf(out) + al;
			}
		}
		// file.write(out.toString());
		return out.toString();
	}

	/**
	 * Letter encode
	 * 
	 * @param num
	 * @return
	 */
	private static int Reverse(int num) {
		if (num < 91 && num > 64) {// A--Z
			num = num + key;
			if (num > 90) {// >Z
				num = 64 + (num - 90);
			} else if (num < 65) {// <A
				num = 91 - (65 - num);
			}
		}

		else if (num > 96 && num < 123) {// a--z 97--122
			num = num + key;
			if (num > 122) {// >Z
				num = 96 + (num - 122);
			} else if (num < 97) {// <A
				num = 123 - (97 - num);
			}
		} else {
			return num;
		}
		return num;
	}
}
