package lab3;

import java.util.ArrayList;
import java.math.*;
public class RSA {
	
//	static int p=47;
//	static int q=59;
	static BigInteger n;
	static BigInteger e;//public key
	static BigInteger d;//private key
//	static int z = (p-1) * (q-1);
	/**
	 * ��ʼ��������ֵ
	 * @param pair
	 */
	public static void iniNum(RSAKeyPair pair){
		n=pair.getPublicKey().getN();
		d=pair.getPublicKey().getD();
		e=pair.getPrivateKey().getE();
	}
	public static void PublicIniNum(RSA_publicKey Pkey){
		n=Pkey.getN();
		d=Pkey.getD();
	}
	public static void PrivateIniNum(RSA_privateKey Prkey){
		e=Prkey.getE();
		n=Prkey.getN();
	}
	/**
	 * �����������Ϣ����string ��Ϣת��ΪASCII�����Ϣ������ٰ��ַ�����������ս��������µ��ַ����з���
	 * @param mes
	 * @return
	 */
	public static ArrayList<String>  dealEnMes(String mes){
		ArrayList<String> list=new ArrayList<String>();
		for(int i=0;i<mes.length();i++){
			int num=(int) mes.charAt(i);//ת����ASCII���Ӧ��������ʽ
			list.add(encript(String.valueOf(num)));
		}
		System.out.println(list);
		return list;//���ܴ�����message
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
	 * ������ܺ��������Ϣ������б���д�����ȡ���յ�������Ϣ
	 * @param list
	 * @return
	 */
	public static String dealDeMes(ArrayList<String> list){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<list.size();i++){
			int st=Integer.valueOf(decript(list.get(i)));//��ȡstring���͵Ĵ���������
			sb.append((char)st);
		}
		System.out.println(sb.toString());
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
