package lab3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;






/**
 * 6.2:两用户之间的分享以及加密的问题已经解决，接下来需要针对多个用户共同在线的操作进行验证，同时准备UI 以及DES等加密算法
 * @author dell
 *
 */
public class Chat_Client {
	/*
	 * setting the socket and Other global variables
	 */
	private static Socket clientSocket;
	
	private static String key = "MYSECRETKEY";// Default play fair key
	private String name;

	private static final int port = 9999;
	private static final String serverIP = "127.0.0.1";

	private static RSAKeyPair pair;
	
	private static RSA_publicKey Pkey;
	private static String systemmetic_key;
	private static String BF_key;
	
	/* 密钥分开存储是否更好一些？
	 * 
	 * 用于存储哪种加密方式的密钥
	 */
	private String secretKey;
	/*
	 * 用于存储Caesar的密钥
	 */
    private static int K = 3;// Initially set the key to the Caesar password
	
    
    Chat_Client() throws Exception {

		Scanner sc = new Scanner(System.in);
		setName(sc);
	}

	public static void main(String[] args) throws Exception {

		clientSocket = new Socket(serverIP, port);
		Chat_Client client = new Chat_Client();
		client.start();
	}

	/**
	 * When a user sends a message,mainly implemented functions and methods
	 * 发送消息首先需要将逻辑结构搭好，之后才能有序得发送消息实现消息得传输
	 */
	public void start() {
		try {
			Scanner scanner = new Scanner(System.in);

			// The thread that receives the message sent from the server starts
			ExecutorService exec = Executors.newCachedThreadPool();
			exec.execute(new ListenrS());

			// Create an output stream to send messages to the server
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
			pair = RSA_generateKey.generatorKey(12);// 创建初始的private key 以及 public key
			/*
			 * 此处加密解密出现问题，针对两个用户的操作应该进行公钥以及私钥的分开解密，因此RSA加密解密错误										
			 */
			RSA.iniNum(pair);// 赋值操作，为下一步对密钥加密处理做准备
			pw.println(pair.getPublicKey().toString());
			/*
			 * 主要的输入信息操作均在这里，修改时应注意封装
			 */
			while (true) {

				System.out.println("a.send message to chat(input a)    b.get the online usernames(input b)");
				System.out.println("----------------------------------");
				String operator = scanner.nextLine();
				String choice;
//				choice = Integer.parseInt(operator);
				choice=operator;
				/*
				 * 结合choice 选择进行输入的判断
				 */
				System.out.println("\ninput the user name you want to chat: ");
			    String username = scanner.nextLine();
					pw.println("a" + ":" + username );
					/*
					 * choice a. get the public key 在获取public key之后再进行传递密码的操作
					 * 
					 * -->选择交流的人（获取public key）
					 *    -->选择传递的密钥的方式，并把密钥传过去
					 *       -->确定密钥以及加密的方式后，传递过去所要交流的信息
					 *       
					 *  6.11 此处增加文件传输操作  文件进行HASH value的传输并进行确认
					 *  
					 *  基本选项为：
					 *   1. 输入用户名以及密码(密码操作待确定)  生成初始的用户的公共密钥以及私人密钥 (1. RSA 2. DH)
					 *   2. 进行选择用户的操作(选择当前在线的用户)在确定用户的信息之后自动获取用户的公共密钥(是否需要传输加密的方式待确定)
					 *   3. 进行选择传输信息的选择(1. 基本信息传输message 2. 文件的传输(1.文本 2.图片))
					 *     3.1 普通消息传输(1.Caesar 2. Playfair 3.DES 4.Blowfish)
					 *       3.1.1 选择加密方式
					 *       3.1.2 传输密钥
					 *       3.1.3 传输加密信息
					 *     3.2 文件传输(hash MD5加密方式，通过hash value的确认实现文件的传输确认)：
					 *       3.2.1 传输密钥
					 *       3.2.2 文本文件传输(String传输)
					 *       3.2.3 图片文件的传输(Byte[]传输)
					 */
				switch (choice) {
				case "a":

					String ciphertext = null;
						/*
						 *确定RSA双方得public key之后，进行用户选择进行加密得方式，并且对于加密中的密钥进行RSA方式的加密，从而实现share the key 
						 */
					Thread.sleep(500);
					System.out.println("please input the encript info you want :M. Message F. file");
					String infoType=scanner.nextLine();
					if(infoType.equals("M")){
						Thread.sleep(500);
						System.out.println("please input the encript type you want :1. Caesar  2.Playfair  3.DES  4.BlowFish");
						String  type=scanner.nextLine();//获取加密的方式
						/**
						 * 此处密钥的传输出现错误：
						 *   针对各个加密方法的密钥，需要进行RSA加密操作才可以进行传输
						 */
						/*
						 * Caesar
						 */
						if(type.equals("1")){
							System.out.println("Please input the secret key you use in Caesae ");
							String key=scanner.nextLine();
							K=Integer.valueOf(key);
							ArrayList<String> list=new ArrayList<>();
							list=RSA.dealEnMes(key);
							key=list.get(0);//获取特殊密钥，进行传输
							pw.println(type+" "+key+" "+username);
							/*
							 * 输入信息并加密
							 */
							Thread.sleep(500);//间歇停顿
							System.out.println("Please input your message ");
							String info=scanner.nextLine();
							ciphertext=info;
							ciphertext = Caesar.CaeInfo(info, K);
							
						}
						/*
						 * Playfair
						 */
						else if(type.equals("2")){
							System.out.println("Please input the secret key you use in Playfair ");
							key=scanner.nextLine();
							ArrayList<String> list=new ArrayList<>();
							list=RSA.dealEnMes(key);
//							key=list.get(0);//获取特殊密钥，进行传输
							/**
							 * 此处的RSA 加密解密方式错误
							 */
							System.out.println(RSA.dealDeMes(list));
							String keys=list.get(0);
							for(int i=1;i<list.size();i++){
								keys=keys+"BENZ"+list.get(i);
							}
							pw.println(type+" "+keys+" "+username);
							/*
							 * 输入信息并加密
							 */
							Thread.sleep(500);//间歇停顿
							System.out.println("Please input your message ");
							String info=scanner.nextLine();
							ciphertext=info;
							ciphertext = PlayFair.encrypt(info, key);
						}
						/*
						 * DES
						 */
						else if(type.equals("3")){
							System.out.println("Please input the secret key you use in DES ");
							systemmetic_key=scanner.nextLine();
							ArrayList<String> list=new ArrayList<>();
							list=RSA.dealEnMes(systemmetic_key);
//							key=list.get(0);//获取特殊密钥，进行传输
							String keys=list.get(0);
							for(int i=1;i<list.size();i++){
								keys=keys+"BENZ"+list.get(i);
							}
							pw.println(type+" "+keys+" "+username);
							/*
							 * 输入信息并加密
							 */
							Thread.sleep(500);//间歇停顿
							System.out.println("Please input your message ");
							String info=scanner.nextLine();
							ciphertext = Des.encrypt(info, systemmetic_key, "encrypt");
						}
						/*
						 * blowfish
						 */
						else if(type.equals("4")){
							System.out.println("Please input the secret key you use in IDEA ");
							BF_key=scanner.nextLine();
							ArrayList<String> list=new ArrayList<>();
							list=RSA.dealEnMes(BF_key);
//							key=list.get(0);//获取特殊密钥，进行传输
							String key=list.get(0);
							for(int i=1;i<list.size();i++){
								key=key+"BENZ"+list.get(i);
							}
							pw.println(type+" "+key+" "+username);
							/*
							 * 输入信息并加密
							 */
							Thread.sleep(500);//间歇停顿
							System.out.println("Please input your message ");
							String info=scanner.nextLine();
							Blowfish bf=new Blowfish(BF_key);
							ciphertext=bf.encryptString(info);
						
						}
					/*
					 * replace the enter
					 */
					 ciphertext = ciphertext.replace("\n", "/*1111*/");
					pw.println("@" + username + ":" + ciphertext);
					Thread.sleep(500);
					}
					/*
					 * 此处进行文件的传输
					 */
					else if(infoType.equals("F")){
						
					}
					
					
					break;
					
				case "b": //					尝试在这个分支实现获取当前在线人数
					pw.println("b" + ":" + username );
					break;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (clientSocket != null) {
				try {
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setName(Scanner scan) throws Exception {

		PrintWriter pw = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
		// Create input stream
		BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));

		while (true) {
			System.out.println("client starts successfully...：");
			System.out.println("\nPlease input your username：");
			name = scan.nextLine();
			if (name.trim().equals("")) {
				System.out.println("the name must not be empty ");
			} else {
				pw.println(name);
				Thread.sleep(500);
				String pass = br.readLine();
				if (pass != null && (!pass.equals("OK"))) {
					System.out.println("name is already occupied. Please re-enter：");
				} else {
					System.out.println("name “" + name + "”   is created successfully");

					break;
				}
			}
		}
	}

	// The loop reads the messages sent by the server and outputs them to the
	// console on the client side
	/**
	 * 接收信息较为方便，只需判断其信息得格式，然后进行处理即可
	 * @author dell
	 *
	 */
	class ListenrS implements Runnable {

		@Override
		public void run() {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
				String msgString = "";

				while ((msgString = br.readLine()) != null) {

					if (msgString.startsWith("[SystemMessage]")) {
						System.out.println(msgString);
						continue;
					}
					else if(msgString.startsWith("[PublicKey]")){
//						System.out.println(msgString);
						String[] str=msgString.split("]");
						String[] strs=str[1].split(" ");
						String sender=strs[0];
						BigInteger n=new BigInteger(strs[1]);
						BigInteger d=new BigInteger(strs[2]);
						System.out.println(sender+"'s public key is ("+n.toString()+","+d.toString()+")");
						Pkey=new RSA_publicKey(n, d);
						RSA.PublicIniNum(Pkey);
					}
					/**
					 * 此处匹配出现问题，startwith函数的使用问题：吸取教训
					 */
					/*
					 * Caesar secret key
					 */
					else if (msgString.startsWith("Caesar")) {
						System.out.println("[Encript form] "+msgString);
						String info[]=msgString.split(" ");
						K=Integer.valueOf(info[2]);
					}
					/*
					 * Playfair secret key
					 */
					else if (msgString.startsWith("PlayFair")) {
						System.out.println("[Encript form and] "+msgString);
						String info[]=msgString.split(" ");
						String keyss=info[2].replaceAll("BENZ", " ");
						String[] keys=keyss.split(" ");
						ArrayList<String> list=new ArrayList<>();
						for(int i=0;i<keys.length;i++){
							System.out.println(keys[i]);
							list.add(keys[i]);
						}
						System.out.println(RSA.d.toString()+" "+RSA.e.toString()+" "+RSA.n.toString());
						key=RSA.dealDeMes(list);
					}
					/*
					 * DES secret key
					 */
					else if (msgString.startsWith("DES")) {
						System.out.println("[Encript form and] "+msgString);
						String info[]=msgString.split(" ");
//						systemmetic_key=info[2];
						String[] keys=info[2].split("BENZ");
						ArrayList<String> list=new ArrayList<>();
						for(int i=0;i<keys.length;i++){
							list.add(keys[i]);
						}
						systemmetic_key=RSA.dealDeMes(list);
					}
					/*
					 * BlowFish secret key
					 */
					else if (msgString.startsWith("BlowFish")) {
						System.out.println("[Encript form and] "+msgString);
						String info[]=msgString.split(" ");
//						BF_key=info[2];
						String[] keys=info[2].split("BENZ");
						ArrayList<String> list=new ArrayList<>();
						for(int i=0;i<keys.length;i++){
							list.add(keys[i]);
						}
						BF_key=RSA.dealDeMes(list);
					}
					/*
					 * Caesar 解密信息
					 * 
					 * 解密后的信息显示出现问题：显示的信息缺少或者冗余
					 */
					else if (msgString.startsWith("MessageCaesar")) {
//						System.out.println(msgString);
						String info[] =msgString.split(" ");
						String mes=msgString.replaceAll(info[0]+" ", "");;
//						System.out.println("mes: "+mes);
						String plain = Caesar.CaeInfo(mes, -K);
						System.out.println("Decrypted ciphertext：" + plain);
					}
					/*
					 * Playfair 解密信息 
					 * 解密后的信息出现问题，无法完全显示
					 * 原因：是因为信息在加密过程中添加的空格所导致的问题
					 */
					else if (msgString.startsWith("MessagePlayFair")) {
						String info[] =msgString.split(" ");
						String mes=msgString.replaceAll(info[0]+" ", "");
//						System.out.println(mes);
						String plain = PlayFair.decrypt(mes, key);// File information and key information are passed in
						System.out.println("Decrypted ciphertext：" + plain);
					}
					/*
					 * DES 解密信息
					 */
					else if (msgString.startsWith("MessageDES")) {
						String info[] =msgString.split(" ");
						String mes=msgString.replaceAll(info[0]+" ", "");
						String plaintext = Des.encrypt(mes,systemmetic_key, "decrypt");
						plaintext = plaintext.replace("*", "");
						System.out.println("Decrypted ciphertext：" + plaintext);
					}
					/*
					 * IDEA 解密信息 :
					 * 此处解密出现问题，对于加密后的信息无法实现正常的解密
					 */
					else if (msgString.startsWith("MessageBlowFish")) {
						String info[] =msgString.split(" ");
						String mes=msgString.replaceAll(info[0]+" ", "");
					   Blowfish bf=new Blowfish(BF_key);
//					   System.out.println("ENMES: "+mes);
					   String plain=bf.decryptString(mes);
						System.out.println("Decrypted ciphertext：" + plain);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}