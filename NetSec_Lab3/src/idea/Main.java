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
		 * �������Կ��Ϣ
		 */
		String key = "1234567891234567";
//		String key = "12345678";
//		String data = "11111111";
		/*
		 * ��Ҫ���ܵ���Ϣ
		 */
		String data = "123abcde";
//		�ji�c��?
		/*
		 * ��Կ�Լ�����ת��Ϊbyte��ʽ
		 */
		byte[] bytekey = key.getBytes();
		byte[] bytedata = data.getBytes();
		/*
		 * ������Ϣ   true/false�����ж�
		 * ���ֽڽ��м��ܵĲ�����
		 * true ������
		 */
		byte[] encryptdata = IDEA.IDEAEncrypt(bytekey, bytedata, true);
		/*
		 * ������Ϣ
		 * false ������
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
