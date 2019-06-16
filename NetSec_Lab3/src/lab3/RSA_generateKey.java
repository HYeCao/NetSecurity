package lab3;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * 初始化RSA中的密钥
 * 
 * @author dell
 *
 */
public class RSA_generateKey {

	public static RSAKeyPair generatorKey(int bitlength) {

		SecureRandom random = new SecureRandom();

//		random.setSeed(new Date().getTime());

		BigInteger p, q;

		while (!(p = BigInteger.probablePrime(bitlength, random)).isProbablePrime(1)) {
			continue;
		} 

		while (!(q = BigInteger.probablePrime(bitlength, random)).isProbablePrime(1)) {
			continue;
		} 
		
		BigInteger n = p.multiply(q);// generate n

		/*
		 * get z
		 */
		BigInteger z = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

//		BigInteger e = BigInteger.probablePrime(bitlength - 1, random);
		BigInteger e=GetE(z,bitlength);
		while(e==null){
			e=GetE(z,bitlength);
		}

		BigInteger d = GetKey(z, e);//get the private key 

		RSA_publicKey   publicKey = new RSA_publicKey(n, d);
		RSA_privateKey  privateKey= new RSA_privateKey(n, e);
		System.out.println("your private key :"+privateKey.toString());
		System.out.println("your public key:"+publicKey.toString());
		// Generate the secret key pair to return the key pair
		return new RSAKeyPair(privateKey,publicKey );
	}

	/**
	 * 根据已知信息来获取private key
	 * 
	 * @param strE
	 * @return
	 */
	public static BigInteger GetKey(BigInteger z, BigInteger e) {
		// e = Integer.valueOf(strE);// get the value of e
		BigInteger PrivateKey = new BigInteger("1");
		BigInteger n=new BigInteger("1");
		while (true) {
			if(z.multiply(n).add(BigInteger.ONE).mod(e).intValue()==0){
				PrivateKey = z.multiply(n).add(BigInteger.ONE).divide(e);
				return PrivateKey;
			}
			n=n.add(BigInteger.ONE);
		}
	}
	/**
	 * 随机数 e 的要求：1. 1--z
	 *                 2. prime number
	 *                 3. z/e != 0
	 * @param z
	 * @param len
	 * @return
	 */
	public static BigInteger GetE(BigInteger z,int len){
		SecureRandom random = new SecureRandom();
		 BigInteger e=BigInteger.probablePrime(len - 1, random);
		 if(e.intValue()>1&&e.intValue()<z.intValue()){
			 if(z.divide(e).intValue()!=0){
				 return e;
			 }
		 }
		return null;
	}
}
