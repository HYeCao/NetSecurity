package des_exam;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/**
 * DES encryption method
 */
public class DES {
	public Key key;

	/**
	 * Generating KEY based on parameters
	 * 
	 * @param strKey
	 */
	public void genKey(String strKey) {
		try {
			KeyGenerator _generator = KeyGenerator.getInstance("DES");
			_generator.init(new SecureRandom(strKey.getBytes()));
			this.key = _generator.generateKey();
			_generator = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Encrypted String plaintext input, String ciphertext output
	 * 
	 * @param strMing
	 * @return
	 */
	public String getEncString(String strMing) {
		String strMi = "";
		try {
			return byte2hex(getEncCode(strMing.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return strMi;
	}

	/**
	 * Decryption with String ciphertext input, String plaintext output
	 * 
	 * @param strMi
	 * @return
	 */
	public String getDesString(String strMi) {
		String strMing = "";
		try {
			return new String(getDesCode(hex2byte(strMi.getBytes())));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return strMing;
	}

	/**
	 * Encryption is entered in byte[] plaintext, byte[] ciphertext output
	 * 
	 * @param byteS
	 * @return
	 */
	private byte[] getEncCode(byte[] byteS) {
		byte[] byteFina = null;
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byteFina = cipher.doFinal(byteS);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	/**
	 * The decryption is input in byte[] ciphertext and output in byte[]
	 * plaintext
	 * 
	 * @param byteD
	 * @return
	 */
	private byte[] getDesCode(byte[] byteD) {
		Cipher cipher;
		byte[] byteFina = null;
		try {
			cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byteFina = cipher.doFinal(byteD);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	/**
	 * 二行制转字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) { // A number of bytes，
												// Turn to 16 hexadecimal
												// strings
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			// Integers turn to sixteen decimal representations
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase(); // Turn to uppercase
	}

	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("The length is not even");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			// A two bit group that represents a byte and replaces the 16 string
			// string represented so as a byte.
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}

	public Key getKey() {
		return key;
	}
	public static void main(String[] args) {
		int cn=0;
		int ce;
		String plainmsg="abc",ciphermsg;
		DES des = new DES();
		/*
		 * 初始化创建密钥
		 */
		des.genKey(cn + "");
		Key ckey = des.getKey();//获取密钥
		/*
		 * 加密信息
		 */
		ciphermsg = des.getEncString(plainmsg);
		System.out.println(ciphermsg);
		/*
		 * 解密信息
		 */
		String plain = des.getDesString(ciphermsg);
		System.out.println(plain);
	}
}
