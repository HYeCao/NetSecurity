package lab3_2_dh;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
 * 
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
	private static RSA_privateKey Prkey;

	private static String systemmetic_key;// DES
	private static String BF_key;// BlowFish

	private static String hashVal;
	
	private static String enfor;

	private static String mA;
	private static String mB;
	private static String DHsecret;
	/*
	 * 密钥分开存储是否更好一些？
	 * 
	 * 用于存储哪种加密方式的密钥
	 */
	// private String secretKey;
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
			pair = RSA_generateKey.generatorKey(12);// 创建初始的private key 以及
													// public key
			Pkey = pair.getPublicKey();
			Prkey = pair.getPrivateKey();
			// 赋值操作，为下一步对密钥加密处理做准备
			String ins=pair.getPublicKey().toString();
			System.out.println("Please input the encript form you use:");
			String enForm=scanner.nextLine();
			System.out.println("Please input the hash alg you use:");
			String hashForm=scanner.nextLine();
			System.out.println("Please input the Certificate time you want(like: 2h/2days/2months):");
			String dayForm=scanner.nextLine();
			System.out.println("Please input the secret num in the DH");
			String a=scanner.nextLine();
			mA=DH.genPublicKey(a);//mA公钥创建成功
			ins=ins+" "+enForm+" "+hashForm+" "+dayForm+" "+mA;
			System.out.println("----------------------------------");
			pw.println(ins);
			/*
			 * 主要的输入信息操作均在这里，修改时应注意封装
			 */
			while (true) {

				System.out.println("a.send message to chat(input a)  b.get the online usernames(input b)");
				System.out.println("----------------------------------");
				String operator = scanner.nextLine();
				String choice;
				// choice = Integer.parseInt(operator);
				choice = operator;

				/*
				 * choice a. get the public key 在获取public key之后再进行传递密码的操作
				 * 
				 * -->选择交流的人（获取public key） -->选择传递的密钥的方式，并把密钥传过去
				 * -->确定密钥以及加密的方式后，传递过去所要交流的信息
				 * 
				 * 6.11 此处增加文件传输操作 文件进行HASH value的传输并进行确认
				 * 
				 * 基本选项为： 1. 输入用户名以及密码(密码操作待确定) 生成初始的用户的公共密钥以及私人密钥 (1. RSA 2.
				 * DH) 2.
				 * 进行选择用户的操作(选择当前在线的用户)在确定用户的信息之后自动获取用户的公共密钥(是否需要传输加密的方式待确定) 3.
				 * 进行选择传输信息的选择(1. 基本信息传输message 2. 文件的传输(1.文本 2.图片)) 3.1
				 * 普通消息传输(1.Caesar 2. Playfair 3.DES 4.Blowfish) 3.1.1 选择加密方式
				 * 3.1.2 传输密钥 3.1.3 传输加密信息 3.2 文件传输(hash MD5加密方式，通过hash
				 * value的确认实现文件的传输确认)： 3.2.1 传输密钥 3.2.2 文本文件传输(String传输) 3.2.3
				 * 图片文件的传输(Byte[]传输)
				 * 
				 * 全局的变量不要与局部的变量混用，容易造成混乱 每种加密的输入输出一定要明确清楚
				 */
				switch (choice) {
				case "a":
					/*
					 * 结合choice 选择进行输入的判断
					 */
					System.out.println("\ninput the user name you want to chat: ");
					String username = scanner.nextLine();
					pw.println("a" + ":" + username);
					String ciphertext = null;
					
					/*
					 * 
					 * 在此处进行添加修改证书中加密方式的操作
					 * 
					 */
					
					
					
					/*
					 * 确定RSA双方得public
					 * key之后，进行用户选择进行加密得方式，并且对于加密中的密钥进行RSA方式的加密，从而实现share the
					 * key
					 */
					System.out.println("please choose RSA or DH to pass the secret key: R.rsa  D.dh");
					enfor=scanner.nextLine();//读取针对密钥的加密方式的方式
					/*
					 * 当用户选择使用DH 进行加密时
					 */
					if(enfor.equals("D")){
						System.out.println("please input your secret num a to make the DH secret key");
						mA=scanner.nextLine();
						System.out.println("so your secret key is "+mA+" you should use it as your secret key");
					}
					pw.println("DH used to transfer the key");
					Thread.sleep(500);
					System.out.println("please input the encript info you want :M. Message F. file");
					String infoType = scanner.nextLine();
					if (infoType.equals("M")) {

						/*
						 * 确定RSA双方得public
						 * key之后，进行用户选择进行加密得方式，并且对于加密中的密钥进行RSA方式的加密，从而实现share
						 * the key
						 */
						Thread.sleep(500);
						/*
						 * 此处修改：修改为当前已经获取到了加密方式，只需输入密钥即可
						 */
						
						System.out.println(
								"please input the encript type you use :1. Caesar  2.Playfair  3.DES  4.BlowFish");
						String type = scanner.nextLine();// 获取加密的方式
						/*
						 * Caesar
						 */
						if (type.equals("1")) {
							System.out.println("Please input the secret key you use in Caesae ");
							String keys = scanner.nextLine();
							K = Integer.valueOf(keys);
							ArrayList<String> list = new ArrayList<>();
							list = RSA.dealEnMes(keys, Pkey);
							System.out.println(list);
							pw.println(type + " " + list.get(0) + " " + username);
							/*
							 * 输入信息并加密
							 */
							Thread.sleep(500);// 间歇停顿
							System.out.println("Please input your message ");
							String info = scanner.nextLine();
							ciphertext = info;
							ciphertext = Caesar.CaeInfo(info, K);

						}
						/*
						 * Playfair
						 */
						else if (type.equals("2")) {
							System.out.println("Please input the secret key you use in Playfair ");
							key = scanner.nextLine();
							ArrayList<String> list = new ArrayList<>();
							list = RSA.dealEnMes(key, Pkey);
							// key=list.get(0);//获取特殊密钥，进行传输
							/**
							 * 此处的RSA 加密解密方式错误
							 */
							// System.out.println(rsa.d.toString()+"
							// "+rsa.e.toString()+" "+rsa.n.toString());
							// RSA.dealDeMes(list,Prkey);
							String keys = list.get(0);
							for (int i = 1; i < list.size(); i++) {
								keys = keys + "BENZ" + list.get(i);
							}
							pw.println(type + " " + keys + " " + username);
							/*
							 * 输入信息并加密
							 */
							Thread.sleep(500);// 间歇停顿
							System.out.println("Please input your message ");
							String info = scanner.nextLine();
							ciphertext = info;
							ciphertext = PlayFair.encrypt(info, key);
						}
						/*
						 * DES: 加密要求：初始的密钥长度为8为(64bit)
						 */
						else if (type.equals("3")) {
							System.out.println("Please input the secret key you use in DES (8 nums/letters)");
							systemmetic_key = scanner.nextLine();
							ArrayList<String> list = new ArrayList<>();
							list = RSA.dealEnMes(systemmetic_key, Pkey);
							String keys = list.get(0);
							for (int i = 1; i < list.size(); i++) {
								keys = keys + "BENZ" + list.get(i);
							}
							pw.println(type + " " + keys + " " + username);
							/*
							 * 输入信息并加密
							 */
							Thread.sleep(500);// 间歇停顿
							System.out.println("Please input your message ");
							String info = scanner.nextLine();
							ciphertext = Des.encrypt(info, systemmetic_key, "encrypt");
							// System.out.println(ciphertext);
						}
						/*
						 * blowfish
						 */
						else if (type.equals("4")) {
							System.out.println("Please input the secret key you use in IDEA ");
							BF_key = scanner.nextLine();
							ArrayList<String> list = new ArrayList<>();
							list = RSA.dealEnMes(BF_key, Pkey);
							// key=list.get(0);//获取特殊密钥，进行传输
							String key = list.get(0);
							for (int i = 1; i < list.size(); i++) {
								key = key + "BENZ" + list.get(i);
							}
							pw.println(type + " " + key + " " + username);
							/*
							 * 输入信息并加密
							 */
							Thread.sleep(500);// 间歇停顿
							System.out.println("Please input your message ");
							String info = scanner.nextLine();
							Blowfish bf = new Blowfish(BF_key);
							ciphertext = bf.encryptString(info);

						}
						// Thread.sleep(500);
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
					else if (infoType.equals("F")) {
						// Thread.sleep(500);
						System.out.println("please input the file type you want :T. txt  J.jpg/bmp");
						String type = scanner.nextLine();// 获取加密的方式
						if (type.equals("T")) {
							/*
							 * 文本文件的传输：基本思路 1.获取输入的文件名 2.根据文件名获取文件的信息
							 * 3.根据文件信息进行MD5的hash编码 3.1并将hash
							 * value加密成为密钥(RSA方式(Public key private key 的方式))
							 * 4.传输加密后的hash value至另一个客户端 5.传输hash
							 * value+文件信息，在另一个接收的客户端进行确认 5.1客户端信息确认，先对加密过的hash
							 * value进行解密操作 5.2解密后的hash value对发送过来的hash value
							 * 进行对比，如果一致可以确认文件的信息是正确的
							 * 
							 * 问题：在进行文件的读写操作时是否会导致线程产生报错
							 */
							System.out.println("please input the filename you want to transfer");
							String filename = scanner.nextLine();
							File file = new File(filename);
							FileInputStream fi = new FileInputStream(file);
							int ch = 0;
							String info = new String();
							while ((ch = fi.read()) != -1) {
								info += (char) ch; // info里面存储的为文件内的信息
							}

							fi.close();
							MD5 md = new MD5();
							String hashValue = md.digest(info);// 1.获取hash value
							ArrayList<String> list = new ArrayList<>();
							list = RSA.dealEnMes(hashValue, Pkey);
							String keys = list.get(0);
							/*
							 * 14：09 已经实现了加密后的hash
							 * value的传输，但是直接下线了说明读写出现了问题，或者有资源没有关闭
							 */
							for (int i = 1; i < list.size(); i++) {
								keys = keys + "BENZ" + list.get(i);// 2.经过RSA加密后的信息
							}
							System.out.println("the hash value is "+hashValue);
							pw.println("H" + " " + keys + " " + username);// 3.首先将加密过的信息发送给服务器传至客户端
							/*
							 * 此处思考：加密的hash value与hash value + txt
							 * 信息是否需要分开接收，之后再进行对比
							 */
							Thread.sleep(500);// 间歇停顿

							System.out.println("please enter to confirm the file: " + filename);
							String enter = scanner.nextLine();
							String infoTxt = new String();
							
							/**
							 * 换行符 ：\n 回车换行符：\r\n
							 */
							info = info.replace("\r\n", "/*1111*/");// 文本信息去除回车换行符
							infoTxt = hashValue + "BENZ" + info;// 4. hash value
																// 与文本信息之间用BENZ字符串进行分割
							/**
							 * 已经读出信息
							 * 79CFEB94595DE33B3326C06AB1C7DBDABENZabcd123
							 * 
							 * 但是还是出现掉线的情况： 1.有可能是文件读取资源与线程之间的冲突 2.有可能信息传输的错误
							 * 3.服务器端出现基础处理代码错误
							 * 
							 * 解决方案：检查接收信息端是否接收了信息
							 */
							
							pw.println("T" + " " + infoTxt + " " + username);// 5.
																				// 以T
																				// 开头发送hash信息+文本信息
							Thread.sleep(500);
						}

						/*
						 * 图片文件的传输实现:基本实现为基于字节传输的实现
						 *  
						 * 图片传输流程：1.根据输入的文件名处理文件获取hash value 
						 *              2. 将hash value加密进行发送
						 *              3.将未加密的hash value单独以string 形式发送给客户
						 *              4.确认信息均以string形式发送完成，图片单独以字节形式进行发送
						 *              
						 * 完成要点：1.分开实现，先把hash value信息加密并发送
						 *           2.把hash value发送
						 *              (以上两部基本套文本文件的传输形式)
						 *           3.在以上两步实现之后单独进行图片的发送
						 */
						else if (type.equals("J")) {
							System.out.println("please input the filename you want to transfer");
							String filename = scanner.nextLine();
							File file = new File(filename);
							FileInputStream fi = new FileInputStream(file);
							
							//A. 将hash value进行加密并发送
							int ch = 0;
							String info = new String();
							while ((ch = fi.read()) != -1) {
								info += (char) ch; // info里面存储的为文件内的信息 
								                   //对于图片的格式的string 信息，此信息只是作为MD5加密操作，其string数据无实际意义
							}
							fi.close();//此处的关闭操作需要进一步考虑
							MD5 md = new MD5();
							String hashValue = md.digest(info);// 1.获取hash value
							ArrayList<String> list = new ArrayList<>();
							list = RSA.dealEnMes(hashValue, Pkey);
							String keys = list.get(0);
							/*
							 * 14：09 已经实现了加密后的hash
							 * value的传输，但是直接下线了说明读写出现了问题，或者有资源没有关闭
							 */
							for (int i = 1; i < list.size(); i++) {
								keys = keys + "BENZ" + list.get(i);// 2.经过RSA加密后的信息
							}
							
							System.out.println("the hash value is "+hashValue);
							pw.println("H" + " " + keys + " " + username);// 3.首先将加密过的信息发送给服务器传至客户端
							Thread.sleep(500);
							int fileLen = 0;
							/**
							 * 此处思考：是否是文件太大导致文件的读取大小无法实习完整的读取
							 */
							fileLen = (int) file.length();
							System.out.println(fileLen);
							//B.对于未加密的hash value发送(以string格式)
							pw.println("J" + " " + hashValue + " " + username+" "+fileLen);
							Thread.sleep(500);
							
						
							//C. 图片文件的传输
							//以下为图片传输格式
							fi = new FileInputStream(file);
							
							
//							int fileLen = fi.available();//获取到文件的大小
							       
							/*
							 * 此处图片传输的修改：
							 *  先传输过去图片大小，之后再传输文件
							 */
							byte[] buf = new byte[fileLen];
							fi.read(buf);
							
							OutputStream out = clientSocket.getOutputStream();//获取传输的字节传输方式
							/*
							 * 14:35 图片传输解决，但是出现 了多余信息传递至服务器端的处理，因此需要解决
							 */
							out.write(buf,0,fileLen);//字节传输
							fi.close();
							/**
							 * 1.输入流或者输出流只要一者关闭，两个流以及socket都不可以用了
							 * 2.最好的做法是，要关闭则一起关闭：
							 */
//							out.close();
							
							
						}
					}

					break;
				case "b": // 尝试在这个分支实现获取当前在线人数
					pw.println("b" + ":" + name);
					Thread.sleep(500);
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
	 * 
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
					// [OnlineUsers:]
					else if (msgString.startsWith("[OnlineUsers:]")) {
						System.out.println(msgString);
						continue;
						
					}
					/*
					 * [Certificate]li public key alg hash time
					 */
					else if (msgString.startsWith("[Certificate]")) {
						// System.out.println(msgString);
						String[] str = msgString.split("]");
						String[] strs = str[1].split(" ");
						String sender = strs[0];
						BigInteger n = new BigInteger(strs[1]);
						BigInteger e = new BigInteger(strs[2]);
						String algForm=strs[3];
						String hashForm=strs[4];
						String time =strs[5];
						String dhKey=strs[6];
						Certificate cer=new Certificate(strs[1]+" "+strs[2], algForm, hashForm, time,dhKey);
						System.out.println(sender +" "+ cer.toString());
						Pkey = new RSA_publicKey(n, e);
						DHsecret=DH.DHB(dhKey);//获取当前的密钥
					}
					/**
					 * 此处匹配出现问题，startwith函数的使用问题：吸取教训
					 */
					/*
					 * 经过RSA加密后的hash value的解密处理
					 */
					else if (msgString.startsWith("Hash")) {
						// System.out.println("[Encript form] "+msgString);
						String info[] = msgString.split(" ");
						String keyss = info[2].replaceAll("BENZ", " ");
						String[] keys = keyss.split(" ");
						ArrayList<String> list = new ArrayList<>();
						for (int i = 0; i < keys.length; i++) {
							// System.out.println(keys[i]);
							list.add(keys[i]);
						}
						hashVal = RSA.dealDeMes(list, Prkey);
						// hashVal=info[2];
						/*
						 * 此处获取到的加密后的hash value 处理之后正确
						 */
						System.out.println("[Encript form] Hash  [hash value] " + hashVal);
					}
					// TextFile
					/*
					 * text file :hash value+BENZ+info 此处信息没有显示：
					 * 1.先检查是信息错误还是前一步服务器端的错误 2.检查基本全局变量的错误
					 */
					else if (msgString.startsWith("TextFile")) {

						String info[] = msgString.split(" ");

						String[] keys = info[1].split("BENZ");
						// System.out.println(info[1]+" "+hashVal);
						String hash = keys[0];// hash value
						String inf = keys[1];// text message
						inf = inf.replace("/*1111*/", "\r\n");
						/*
						 * 当解密后的hash value 与传输过的hash value一致
						 * 可以确认文本信息一样，因此可以创建文件并将信息写入
						 */

						if (hash.equals(hashVal)) {
							/*
							 * 此处文件的写入问题：新创建的写入到文件读写资源，是否会导致当前线程出错
							 */
							File file = new File("2.txt");

							// if file doesnt exists, then create it
							if (!file.exists()) {
								file.createNewFile();
							}

							FileWriter fw = new FileWriter(file.getAbsoluteFile());
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write(inf);
							bw.close();// 关闭资源是否必须？
							fw.close();

						}
						/*
						 * hash value 不一致 文本信息错误
						 */
						else {
							System.out.println("The two hash values are different!!");
						}
					}
//					JPGFile
					/*
					 * JPGFile文件的传输过程
					 *  流程：1.获取未加密的hash value 信息
					 *        2.与加密的hash value 解密后的信息进行对比确认
					 *        3.图片字节的获取(创建+填充字节)
					 */
					else if (msgString.startsWith("JPGFile")) {

						String info[] = msgString.split(" ");

						String hash = info[1];// hash value
						/*
						 * 当解密后的hash value 与传输过的hash value一致
						 *   
						 *   在hash value 确认正确后，可以进行图片文件的字节接收与创建填充
						 */
						if (hash.equals(hashVal)) {
							/*
							 * 此处文件的写入问题：新创建的写入到文件读写资源，是否会导致当前线程出错
							 *   图片字节没有写入进去
							 */
							File file = new File("2.JPG");

							System.out.println("JPG file creates");
							// if file doesnt exists, then create it
							if (!file.exists()) {
								file.createNewFile();
							}

							/*
							 * 在上述已经实现传输hash基本信息之后，进行图片信息的获取以及传输至另一个客户端
							 */
							InputStream in = clientSocket.getInputStream();
							//创建图片字节流
							FileOutputStream fos = new FileOutputStream("2.JPG");
							byte[] buf = new byte[1024];
							int len = 0;
							//往字节流里写图片数据
							while ((len = in.read(buf)) != -1)
							{
							fos.write(buf,0,len);//图片信息写入至server.JPG之中
							}
							//获取输出流，准备给客户端发送消息
							//关闭资源
							fos.close();
//							in.close();
						}
						/*
						 * hash value 不一致 文本信息错误
						 */
						else {
							System.out.println("The two hash values are different!!");
						}
					}
					
					
					
					
					/*
					 * Caesar secret key
					 */
					else if (msgString.startsWith("Caesar")) {
						// System.out.println("[Encript form] "+msgString);
						System.out.println(msgString);
						String info[] = msgString.split(" ");
						String keys=info[2];
						ArrayList<String> list = new ArrayList<>();
						list.add(keys);
						String skey = RSA.dealDeMes(list, Prkey);
						K = Integer.valueOf(skey);
						System.out.println("[Encript form] Caesar  [secret key] " + K);
					}
					/*
					 * Playfair secret key
					 */
					else if (msgString.startsWith("PlayFair")) {
						// System.out.println("[Encript form and] "+msgString);
						String info[] = msgString.split(" ");
						String keyss = info[2].replaceAll("BENZ", " ");
						String[] keys = keyss.split(" ");
						ArrayList<String> list = new ArrayList<>();
						for (int i = 0; i < keys.length; i++) {
//							System.out.println(keys[i]);
							list.add(keys[i]);
						}
						key = RSA.dealDeMes(list, Prkey);
						System.out.println("[Encript form] PlayFair  [secret key] " + key);
					}
					/*
					 * DES secret key
					 */
					else if (msgString.startsWith("DES")) {
						System.out.println("[Encript form and] " + msgString);
						String info[] = msgString.split(" ");
						// systemmetic_key=info[2];
						String[] keys = info[2].split("BENZ");
						ArrayList<String> list = new ArrayList<>();
						for (int i = 0; i < keys.length; i++) {
							list.add(keys[i]);
						}
						systemmetic_key = RSA.dealDeMes(list, Prkey);
						System.out.println("[Encript form] DES  [secret key] " + systemmetic_key);
					}

					/*
					 * BlowFish secret key
					 */
					else if (msgString.startsWith("BlowFish")) {
						// System.out.println("[Encript form and] "+msgString);
						String info[] = msgString.split(" ");
						// BF_key=info[2];
						String[] keys = info[2].split("BENZ");
						ArrayList<String> list = new ArrayList<>();
						for (int i = 0; i < keys.length; i++) {
							list.add(keys[i]);
						}
						BF_key = RSA.dealDeMes(list, Prkey);
						System.out.println("[Encript form] BlowFish  [secret key] " + BF_key);
					}
					/*
					 * Caesar 解密信息
					 * 
					 * 解密后的信息显示出现问题：显示的信息缺少或者冗余
					 */
					else if (msgString.startsWith("MessageCaesar")) {
						// System.out.println(msgString);
						String info[] = msgString.split(" ");
						String mes = msgString.replaceAll(info[0] + " ", "");
						;
						// System.out.println("mes: "+mes);
						String plain = Caesar.CaeInfo(mes, -K);
						System.out.println("Decrypted ciphertext：" + plain);
					}
					/*
					 * Playfair 解密信息 解密后的信息出现问题，无法完全显示 原因：是因为信息在加密过程中添加的空格所导致的问题
					 */
					else if (msgString.startsWith("MessagePlayFair")) {
						String info[] = msgString.split(" ");
						String mes = msgString.replaceAll(info[0] + " ", "");
						// System.out.println(mes);
						String plain = PlayFair.decrypt(mes, key);// File
																	// information
																	// and key
																	// information
																	// are
																	// passed in
						System.out.println("Decrypted ciphertext：" + plain);
					}
					/*
					 * DES 解密信息
					 */
					else if (msgString.startsWith("MessageDES")) {
						String info[] = msgString.split(" ");
						String mes = msgString.replaceAll(info[0] + " ", "");
						String plaintext = Des.encrypt(mes, systemmetic_key, "decrypt");
						plaintext = plaintext.replace("*", "");
						// System.out.println(systemmetic_key);
						System.out.println("Decrypted ciphertext：" + plaintext);
					}
					/*
					 * IDEA 解密信息 : 此处解密出现问题，对于加密后的信息无法实现正常的解密
					 */
					else if (msgString.startsWith("MessageBlowFish")) {
						String info[] = msgString.split(" ");
						String mes = msgString.replaceAll(info[0] + " ", "");
						Blowfish bf = new Blowfish(BF_key);
						// System.out.println("ENMES: "+mes);
						String plain = bf.decryptString(mes);
						System.out.println("Decrypted ciphertext：" + plain);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}