/**
 * 
 */
package rsa;

import java.math.BigInteger;

/**
 * share the key:
 * 1.ÿ���û���ʼ������������Կ Every client will generate it's own public and private key. 
 * 2.�û�����ʱ�����Ƚ���public key����ʵ����һ��key�Ĵ��� If two clients want to communicate with each other, they will first exchange there public keys. 
 * 3.��������һ����ȡ����Կ������ʵ����Կ�Ĵ��� Then will exchange the secret key using public key. 
 * @author dell
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str="abcABC123";
		/*
		 * 1. ����ÿ���û���public key��private key
		 */
		
		
		RSAKeyPair pair=RSA_generateKey.generatorKey(12);//������ʼ��private key �Լ� public key
//		RSA.iniNum(pair);
//		RSA.dealDeMes(RSA.dealEnMes(str));
//		String a="a1",b="a1",c="1";
//		if(a.startsWith("1"))System.out.println("OMG");
		
	}


}
