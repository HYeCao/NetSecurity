/**
 * 
 */
package des;

/**
 * @author dell
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Des des = new Des();
		String systemmetic_key = "12a45689";
		String plain="acbd456";
		/*
		 * ���ܹ���
		 */
		String ciphertext = des.encrypt(plain, systemmetic_key, "encrypt");
		
		System.out.println(ciphertext);

		/*
		 * ���ܹ���
		 */
		String plaintext = des.encrypt(ciphertext,systemmetic_key, "decrypt");
		plaintext = plaintext.replace("*", "");
		System.out.println(plaintext);
	}

}
