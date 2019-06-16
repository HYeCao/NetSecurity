package lab3_2_dh;

import java.util.ArrayList;
import java.util.List;

public class PlayFair {

	/**
	 * Process plaintext
	 * 
	 * @param P
	 *            plaintext
	 * 
	 * @return Process plaintext
	 */
	public static String dealP(String P) {
		StringBuilder sb = new StringBuilder(P);
		for (int i = 1; i < sb.length(); i = i + 2) {
			if (sb.charAt(i) == sb.charAt(i - 1)) {
				sb.deleteCharAt(i - 1);
				sb.insert(i - 1, "AOX");// double
			}

		}
		// AOX
		if (sb.length() % 2 != 0) {
			sb.append("AOX");
		}
		String p = sb.toString();
		p = p.replaceAll(" ", "BMW");
		// System.out.println("处理后的明文：" + p);
		return p;
	}

	/**
	 * Process key
	 * 
	 * @param K
	 *            key
	 * 
	 * @return keyList<Character>
	 */
	public static List<Character> dealK(String K) {
		List<Character> list = new ArrayList<Character>();
		char[] ch = K.toCharArray();
		int len = ch.length;
		for (int i = 0; i < len; i++) {
			if (!list.contains(ch[i])) {
				list.add(ch[i]);
			}
		}
		return list;
	}

	/**
	 * build matrix 9*9
	 * 
	 * @param K
	 *            key
	 * 
	 * @return 9*9matrix
	 */
	public static char[][] matrix(String K) {
		List<Character> key = dealK(K);
		List<Character> list = new ArrayList<Character>(key);
		for (char ch = 'A'; ch <= 'Z'; ch++) {
			if (!list.contains(ch)) {
				list.add(ch);
			}
		}
		for (char ch = 'a'; ch <= 'z'; ch++) {
			if (!list.contains(ch)) {
				list.add(ch);
			}
		}
		if (!list.contains('{')) {
			list.add('{');
		}
		if (!list.contains(':')) {
			list.add(':');
		}
		if (!list.contains('}')) {
			list.add('}');
		}
		if (!list.contains('~')) {
			list.add('~');
		}
		if (!list.contains('!')) {
			list.add('!');
		}
		if (!list.contains('"')) {
			list.add('"');
		}
		if (!list.contains('$')) {
			list.add('$');
		}
		if (!list.contains('#')) {
			list.add('#');
		}
		if (!list.contains('%')) {
			list.add('%');
		}
		if (!list.contains('&')) {
			list.add('&');
		}
		if (!list.contains('\'')) {
			list.add('\'');
		}
		if (!list.contains('<')) {
			list.add('<');
		}
		if (!list.contains('>')) {
			list.add('>');
		}
		if (!list.contains('*')) {
			list.add('*');
		}
		if (!list.contains('+')) {
			list.add('+');
		}
		if (!list.contains(',')) {
			list.add(',');
		}
		if (!list.contains('-')) {
			list.add('-');
		}
		if (!list.contains('.')) {
			list.add('.');
		}
		if (!list.contains('/')) {
			list.add('/');
		}
		for (char ch = '0'; ch <= '9'; ch++) {
			if (!list.contains(ch)) {
				list.add(ch);
			}
		}
		char[][] matrix = new char[9][9];
		int count = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				matrix[i][j] = list.get(count++);
			}
		}
		System.out.println("According to the key'" + K + "'build matrix：");
		showMatrix(matrix);
		return matrix;
	}

	/**
	 * print matrix
	 * 
	 * @param matrix
	 * 
	 */
	public static void showMatrix(char[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
	}

	/**
	 * According to playfair to encrypt
	 * 
	 * @param matrix
	 * 
	 * @param ch1
	 * 
	 * @param ch2
	 * 
	 * @return
	 */
	public static String encrypt(char[][] matrix, char ch1, char ch2) {
		int r1 = 0, c1 = 0, r2 = 0, c2 = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if (ch1 == matrix[i][j]) {
					r1 = i;
					c1 = j;
				}
				if (ch2 == matrix[i][j]) {
					r2 = i;
					c2 = j;
				}
			}
		}
		StringBuilder sb = new StringBuilder();

		if (r1 == r2) {
			// The two letters of the plain letter pair are on the same line,
			// and the right letter is intercepted
			sb.append(matrix[r1][(c1 + 1) % 9]);
			sb.append(matrix[r1][(c2 + 1) % 9]);
		} else if (c1 == c2) {
			// The two letters of the plain letter pair are in the same column,
			// and the letters below are intercepted.
			sb.append(matrix[(r1 + 1) % 9][c1]);
			sb.append(matrix[(r2 + 1) % 9][c1]);
		} else {
			// Two letters on the diagonal of the rectangle formed by the plain
			// text, arbitrarily choose two directions
			// Each letter in the plain text pair will be replaced by a letter
			// that goes with it and is in the same column as the other letter
			sb.append(matrix[r1][c2]);
			sb.append(matrix[r2][c1]);
		}
		sb.append(' ');
		return sb.toString();
	}

	/**
	 * Encrypt the plaintext
	 * 
	 * @param P
	 * 
	 * @param K
	 * 
	 * @return
	 */
	public static String encrypt(String P, String K) {
		char[] ch = dealP(P).toCharArray();
		char[][] matrix = matrix(K);//
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ch.length; i = i + 2) {
			sb.append(encrypt(matrix, ch[i], ch[i + 1]));
		}
		return sb.toString();
	}

	/**
	 * Decrypt the ciphertext pair according to the playfair algorithm
	 * 
	 * @param matrix
	 * @param ch1
	 * 
	 * @param ch2
	 * 
	 * @return
	 */
	public static String decrypt(char[][] matrix, char ch1, char ch2) {
		// Get the location of the ciphertext pair in the matrix
		int r1 = 0, c1 = 0, r2 = 0, c2 = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if (ch1 == matrix[i][j]) {
					r1 = i;
					c1 = j;
				}
				if (ch2 == matrix[i][j]) {
					r2 = i;
					c2 = j;
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		if (r1 == r2) {
			sb.append(matrix[r1][(c1 - 1 + 9) % 9]);
			sb.append(matrix[r1][(c2 - 1 + 9) % 9]);
		} else if (c1 == c2) {
			sb.append(matrix[(r1 - 1 + 9) % 9][c1]);
			sb.append(matrix[(r2 - 1 + 9) % 9][c1]);
		} else {
			sb.append(matrix[r1][c2]);
			sb.append(matrix[r2][c1]);
		}
		sb.append(' ');
		return sb.toString();
	}

	/**
	 * Decrypt ciphertext
	 * 
	 * @param C
	 * 
	 * @param K
	 * 
	 * @return
	 */
	public static String decrypt(String C, String K) {

		C = C.replaceAll(" ", "");
		char[] ch = C.toCharArray();
		char[][] matrix = matrix(K);
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < ch.length; i = i + 2) {
			sb.append(decrypt(matrix, ch[i], ch[i + 1]));
		}
		String pl = sb.toString().replaceAll(" ", "");
		StringBuilder sbs = new StringBuilder();
		int i = 0;
		for (; i < pl.length() - 3; i++) {
			if (pl.charAt(i) == 'A' && pl.charAt(i + 1) == 'O' && pl.charAt(i + 2) == 'X' && pl.charAt(i + 3) != 0) {
				sbs.append(pl.charAt(i + 3));
				sbs.append(pl.charAt(i + 3));
				i = i + 3;
			} else {
				sbs.append(pl.charAt(i));
			}
		}

		sbs.append(pl.charAt(i));
		sbs.append(pl.charAt(i + 1));
		if (i + 2 < pl.length())
			sbs.append(pl.charAt(i + 2));
		pl = sbs.toString();
		pl = pl.replaceAll("BMW", " ");
		pl = pl.replaceAll("AOX", "");
		return pl;
	}
}
