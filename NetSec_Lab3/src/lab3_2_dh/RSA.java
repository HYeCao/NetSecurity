package lab3_2_dh;

import java.util.ArrayList;

import sun.net.www.content.text.plain;

import java.math.*;
public class RSA {
	
//	static int p=47;
//	static int q=59;
	static BigInteger n;
	static BigInteger e;//public key
	static BigInteger d;//private key
//	static int z = (p-1) * (q-1);
	
	/**
	 * �����������Ϣ����string ��Ϣת��ΪASCII�����Ϣ������ٰ��ַ�������������ս���������µ��ַ����з���
	 * @param mes
	 * @return
	 */
	public static ArrayList<String>  dealEnMes(String mes,RSA_publicKey Pkey){
		n=Pkey.getN();
		e=Pkey.getE();
		ArrayList<String> list=new ArrayList<String>();
		for(int i=0;i<mes.length();i++){
			int num=(int) mes.charAt(i);//ת����ASCII���Ӧ��������ʽ
			list.add(encript(String.valueOf(num)));
		}
//		System.out.println(list);
		return list;//���ܴ������message
	}
	/**
	 * ����Ӧ�ù�ʽ��C=M*e(��) mod n
	 * @param plain
	 * @return
	 */
	public static String  encript(String mess){
		BigInteger message = new BigInteger(mess);
		BigInteger ciphertext = message.modPow(e, n);
//		BigInteger encrypt_text = message.pow(e.intValue()).mod(n);
		
		return ciphertext.toString();
	}
	/**
	 * �������ܺ��������Ϣ������б����д�������ȡ���յ�������Ϣ
	 * @param list
	 * @return
	 */
	public static String dealDeMes(ArrayList<String> list,RSA_privateKey Prkey){
		n=Prkey.getN();
		d=Prkey.getD();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<list.size();i++){
			int st=Integer.valueOf(decript(list.get(i)));//��ȡstring���͵Ĵ����������
			sb.append((char)st);
		}
//		System.out.println("secret key "+sb.toString());
		return sb.toString();
	}
	/**
	 * ����Ӧ�ù�ʽ��M=C*d(��) mod n
	 * @param ciper
	 * @return
	 */
	public static String decript(String cipher){
		BigInteger ciphertext = new BigInteger(cipher);
		BigInteger message = ciphertext.modPow(d, n);
//		BigInteger decode_text = ciphertext.pow(d.intValue()).mod(n);
		return message.toString();
	}
	/**
	 * ������֪��Ϣ����ȡprivate key
	 * @param strE
	 * @return
	 */
//	public static int GetKey(String strE){
//		e=Integer.valueOf(strE);//get the value of e
//		int PrivateKey = 1;
//		while(true){
//			
//			if( (PrivateKey*e) % z == 1){
//				d=PrivateKey;//��ȡ���յ�private key
//				return PrivateKey;
//			}
//			PrivateKey++;
//		}
//	}
}