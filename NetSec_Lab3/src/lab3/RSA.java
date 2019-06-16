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
	 * 初始化变量数值
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
	 * 处理输入的信息，将string 信息转化为ASCII码的信息，其次再按字符逐个处理，最终将处理后的新的字符进行返回
	 * @param mes
	 * @return
	 */
	public static ArrayList<String>  dealEnMes(String mes){
		ArrayList<String> list=new ArrayList<String>();
		for(int i=0;i<mes.length();i++){
			int num=(int) mes.charAt(i);//转化成ASCII码对应的数字形式
			list.add(encript(String.valueOf(num)));
		}
		System.out.println(list);
		return list;//加密处理后的message
	}
	/**
	 * 加密应用公式：C=M*e(幂) mod n
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
	 * 处理加密后的密文信息，针对列表进行处理，获取最终的明文信息
	 * @param list
	 * @return
	 */
	public static String dealDeMes(ArrayList<String> list){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<list.size();i++){
			int st=Integer.valueOf(decript(list.get(i)));//获取string类型的处理后的明文
			sb.append((char)st);
		}
		System.out.println(sb.toString());
		return sb.toString();
	}
	/**
	 * 解密应用公式：M=C*d(幂) mod n
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
	 * 根据已知信息来获取private key
	 * @param strE
	 * @return
	 */
//	public static int GetKey(String strE){
//		e=Integer.valueOf(strE);//get the value of e
//		int PrivateKey = 1;
//		while(true){
//			
//			if( (PrivateKey*e) % z == 1){
//				d=PrivateKey;//获取最终的private key
//				return PrivateKey;
//			}
//			PrivateKey++;
//		}
//	}
}
