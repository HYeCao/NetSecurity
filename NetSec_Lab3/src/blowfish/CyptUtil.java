package blowfish;


public class CyptUtil {
	//密钥关联的key
	private static final String key = "lscfXe5XSC0oX8x";
	//BlowFish实现
	private static final Blowfish blowfish = new Blowfish(key); 
	
	/**
	 * 加密 
	 */
	public static String encrypt(String plainText){
		return blowfish.encryptString(plainText);
	}
	
	/**
	 * 解密 
	 */
	public static String decrypt(String cryptText){
		return blowfish.decryptString(cryptText);
	}
	
	public static void main(String[] args) {
		System.out.println(encrypt("test"));
		System.out.println(decrypt("8c8f905e57b9d5e042424b85259c6e5fe0f6028e3391f962"));
	}
}
