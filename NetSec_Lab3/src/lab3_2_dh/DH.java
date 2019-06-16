package lab3_2_dh;

import java.math.BigInteger;


public class DH {

	/*
	 * 两个质数 固定
	 */
	private static BigInteger p=new BigInteger("23");
	private static BigInteger g=new BigInteger("5");
	/*
	 * 两个操作数，需要输入
	 */
	private static BigInteger a;//secret key
	private static BigInteger b;//secret key
	
	/**
	 * 根据a来构建mA
	 * 根据b来构建mB
	 * @return
	 */
	public static String genPublicKey(String numA){
		
//		 int A = (int)Math.pow(g,a)%p;
		a=new BigInteger(numA);
		BigInteger mA=g.modPow(a, p);
		
		return mA.toString();
	}
	public static String genPrivateKey(String numB){
		b=new BigInteger(numB);
		BigInteger mB=g.modPow(b, p);
		return mB.toString();
				
	}
	/**
	 * 解密操作
	 * @param numB
	 * @return
	 */
	public static String DHB(String numB){
		BigInteger secretKey;
		BigInteger mB=new BigInteger(numB);
		secretKey=mB.modPow(a, p);
		return secretKey.toString();
	}
	/**
	 * 加密操作 
	 * @param numA
	 * @return
	 */
	public static String DHA(String numA){
		BigInteger secretKey;
		BigInteger mA=new BigInteger(numA);
		secretKey=mA.modPow(b, p);
		return secretKey.toString();
	}
}
