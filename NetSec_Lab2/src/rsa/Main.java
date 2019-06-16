/**
 * 
 */
package rsa;

import java.math.BigInteger;

/**
 * share the key:
 * 1.每个用户初始化创建两个密钥 Every client will generate it's own public and private key. 
 * 2.用户交流时，首先交换public key，来实现下一步key的传递 If two clients want to communicate with each other, they will first exchange there public keys. 
 * 3.借助于上一步获取的密钥，将会实现密钥的传递 Then will exchange the secret key using public key. 
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
		 * 1. 创建每个用户的public key和private key
		 */
		
		
		RSAKeyPair pair=RSA_generateKey.generatorKey(12);//创建初始的private key 以及 public key
//		RSA.iniNum(pair);
//		RSA.dealDeMes(RSA.dealEnMes(str));
//		String a="a1",b="a1",c="1";
//		if(a.startsWith("1"))System.out.println("OMG");
		
	}


}
