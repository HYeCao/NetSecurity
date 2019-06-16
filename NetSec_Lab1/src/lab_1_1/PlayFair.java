package lab_1_1;


import java.util.ArrayList;
import java.util.List;

public class PlayFair {

	/**
	 * Process plaintext
	 * 
	 * @param P   
	 *            
	 * @return 
	 */
	public static String dealP(String P) {
		P = P.toUpperCase();
		P = P.replaceAll("[^A-Z#*]", "");
		StringBuilder sb = new StringBuilder(P);
		for (int i = 1; i < sb.length(); i = i + 2) {
			if (sb.charAt(i) == sb.charAt(i - 1)) {
				sb.insert(i, '*');
			}
		}
		// add #
		if (sb.length() % 2 != 0) {
			sb.append('#');
		}
		String p = sb.toString();
		return p;
	}

	/**
	 * processed key
	 * 
	 * @param K key
	 *            
	 * @return keyList<Character>
	 */
	public static List<Character> dealK(String K) {
		K = K.toUpperCase();
		K = K.replaceAll("[^A-Z]", "");
//		K = K.replaceAll("J", "I");
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
	 * The key letters are added to the 7¡Á4 matrix one by one,("*" , "#" )
	 * 
	 * @param K key
	 *            
	 * @return 7*4
	 */
	public static char[][] matrix(String K) {
		List<Character> key = dealK(K);
		// Add the letters that appear in K
		List<Character> list = new ArrayList<Character>(key);
		// the other letters to add
		for (char ch = 'A'; ch <= 'Z'; ch++) {
			if (!list.contains(ch)) {
				list.add(ch);
			}
		}
		list.add('*');
		list.add('#');
		char[][] matrix = new char[7][4];
		int count = 0;
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 4; j++) {
				matrix[i][j] = list.get(count++);
			}
		}
		matrix[6][2] = '*';
		matrix[6][3] = '#';
		System.out.println("According to the key'" + K + "'build matrix£º");
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
			// The two letters of the plain letter pair are on the same line, and the right letter is intercepted
			sb.append(matrix[r1][(c1 + 1) % 4]);
			sb.append(matrix[r1][(c2 + 1) % 4]);
		} else if (c1 == c2) {
			// The two letters of the plain letter pair are in the same column, and the letters below are intercepted.
			sb.append(matrix[(r1 + 1) % 7][c1]);
			sb.append(matrix[(r2 + 1) % 7][c1]);
		} else {
			// Two letters on the diagonal of the rectangle formed by the plain text, arbitrarily choose two directions
			// Each letter in the plain text pair will be replaced by a letter that goes with it and is in the same column as the other letter
			sb.append(matrix[r1][c2]);
			sb.append(matrix[r2][c1]);
		}
		sb.append(' ');
		return sb.toString();
	}

	/**
	 * Encrypt the plaintext
	 * 
	 * @param P plaintext
	 *            
	 * @param K key
	 *            
	 * @return Ciphertext
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
	 * @return plaintext
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
			sb.append(matrix[r1][(c1 - 1 + 4) % 4]);
			sb.append(matrix[r1][(c2 - 1 + 4) % 4]);
		} else if (c1 == c2) {
			sb.append(matrix[(r1 - 1 + 7) % 7][c1]);
			sb.append(matrix[(r2 - 1 + 7) % 7][c1]);
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
	 * @param C ciphertext
	 *            
	 * @param K key
	 *            
	 * @return Ã÷ÎÄ
	 */
	public static String decrypt(String C, String K) {
		
		C = C.toUpperCase();
		C = C.replaceAll("[^A-Z*#]", "");
//		C=C.replaceAll(" ", "");
		char[] ch = C.toCharArray();
		char[][] matrix = matrix(K);
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < ch.length; i = i + 2) {
			sb.append(decrypt(matrix, ch[i], ch[i + 1]));
		}
		return sb.toString();
	}


}
