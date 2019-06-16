package rsa_example;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Scanner;

public class RSA {
	
	//选择两个素质数p和q
	private static final Integer p = 1987;
	private static final Integer q = 1989;
	private static final Integer e = 13;
	private static final Integer n = p * q;
	private static final Integer z = (p-1) * (q-1);
	
	//计算私钥
	//d = e*mod-1(z)
	//ed*(modz)=1
	//方案一
//	BigInteger random_prime = new BigInteger(e.toString());
//	BigInteger euler = new BigInteger(z.toString());
//	BigInteger privatekey = random_prime.modInverse(euler);
	/**
	 * 计算出私人密钥 d
	 * @return
	 */
	public static Integer calculate_key(){
		
		int private_key = 1;
		while(true){
			
			if( (private_key*e) % z == 1){
				return private_key;
			}
			private_key++;
		}
	}

	/**
	 * 根据公式加密明文
	 * @param plaintext
	 * @param ciphertext
	 * @throws IOException
	 */
	public static void RSA_Encrypt(String plaintext, String ciphertext) throws IOException{
		
		StringBuffer sb = new StringBuffer();
		String s = new String();
		

		
		BufferedReader bf = new BufferedReader(new FileReader(plaintext));
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(ciphertext)));
		
		while((s = bf.readLine())!=null){
			sb.append(s);
//			sb.append("\r\n");
		}
		
		BigInteger message = new BigInteger(sb.toString());
		
//		BigInteger encrypt_text = message.modPow(new BigInteger(e.toString()), new BigInteger(z.toString()));
		BigInteger encrypt_text = message.pow(e).mod(new BigInteger(n.toString()));
		
		pw.write(encrypt_text.toString());
		bf.close();
		pw.close();
		
	}
	/**
	 * 根据公式解密密文
	 * @param ciphertext
	 * @param decodetext
	 * @param private_key
	 * @throws IOException
	 */
	public static void RSA_Decode(String ciphertext, String decodetext, Integer private_key) throws IOException{
		
		StringBuffer sb = new StringBuffer();
		String s = new String();
		
		BufferedReader bf = new BufferedReader(new FileReader(ciphertext));
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(decodetext)));
		//读入全部文本
		while((s = bf.readLine())!=null){
			sb.append(s);
//			sb.append("\r\n");
		}
		
		BigInteger cipher = new BigInteger(sb.toString());
		BigInteger decode_text = cipher.pow(private_key).mod(new BigInteger(n.toString()));
		pw.write(decode_text.toString());
		bf.close();
		pw.close();
		
	}

	public static void main(String[] args) throws IOException {
		// TODO 自动生成的方法存根
		
		String ciphertext = "ciphertext.txt";
		String decodetext = "decodetext.txt";
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Please input the file path of plaintext:");
		System.out.println("For example: plaintext.txt");
		String plaintext = sc.nextLine();
		
		RSA_Encrypt(plaintext, ciphertext);
		System.out.println("\r\nThe encryption is successfully completed.\r\n");
		System.out.println("Please input the private_key to decode:");
		System.out.println("For example: " + calculate_key().toString());
		Integer private_key = sc.nextInt();
		RSA_Decode(ciphertext, decodetext, private_key);
		System.out.println("\r\nThe decryption is successfully completed.");

	}

}
