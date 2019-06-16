/**
 * 
 */
package idea;

import java.io.UnsupportedEncodingException;

/**
 * @author dell
 *
 */
public class Main {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {

		/*
		 * 输入的密钥信息
		 */
		String key = "1234567891234567";
//		String key = "12345678";
//		String data = "11111111";
		/*
		 * 所要加密的信息
		 */
		String data = "123abcde";
//		賘i鎐妮?
		/*
		 * 密钥以及明文转化为byte形式
		 */
		byte[] bytekey = key.getBytes();
		byte[] bytedata = data.getBytes();
		/*
		 * 加密信息   true/false进行判断
		 * 按字节进行加密的操作？
		 * true ：加密
		 */
		byte[] encryptdata = IDEA.IDEAEncrypt(bytekey, bytedata, true);
		/*
		 * 解密信息
		 * false ：解密
		 */
		StringBuilder sb=new StringBuilder();
		char[] ss = new char[128];
		for(int i=0;i<encryptdata.length;i++){
			ss[i]=(char)encryptdata[i];
		}
//		System.out.println();
		byte[] decryptdata=IDEA.IDEAEncrypt(bytekey, encryptdata, false);
		System.out.println(new String(encryptdata));
		System.out.println(new String(decryptdata));
		System.out.println("--------------------------------");
		
	}

}
