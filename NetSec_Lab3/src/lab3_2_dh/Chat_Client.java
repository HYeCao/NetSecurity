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
 * 6.2:���û�֮��ķ����Լ����ܵ������Ѿ��������������Ҫ��Զ���û���ͬ���ߵĲ���������֤��ͬʱ׼��UI �Լ�DES�ȼ����㷨
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
	 * ��Կ�ֿ��洢�Ƿ����һЩ��
	 * 
	 * ���ڴ洢���ּ��ܷ�ʽ����Կ
	 */
	// private String secretKey;
	/*
	 * ���ڴ洢Caesar����Կ
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
	 * ������Ϣ������Ҫ���߼��ṹ��ã�֮���������÷�����Ϣʵ����Ϣ�ô���
	 */
	public void start() {
		try {
			Scanner scanner = new Scanner(System.in);

			// The thread that receives the message sent from the server starts
			ExecutorService exec = Executors.newCachedThreadPool();
			exec.execute(new ListenrS());

			// Create an output stream to send messages to the server
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
			pair = RSA_generateKey.generatorKey(12);// ������ʼ��private key �Լ�
													// public key
			Pkey = pair.getPublicKey();
			Prkey = pair.getPrivateKey();
			// ��ֵ������Ϊ��һ������Կ���ܴ�����׼��
			String ins=pair.getPublicKey().toString();
			System.out.println("Please input the encript form you use:");
			String enForm=scanner.nextLine();
			System.out.println("Please input the hash alg you use:");
			String hashForm=scanner.nextLine();
			System.out.println("Please input the Certificate time you want(like: 2h/2days/2months):");
			String dayForm=scanner.nextLine();
			System.out.println("Please input the secret num in the DH");
			String a=scanner.nextLine();
			mA=DH.genPublicKey(a);//mA��Կ�����ɹ�
			ins=ins+" "+enForm+" "+hashForm+" "+dayForm+" "+mA;
			System.out.println("----------------------------------");
			pw.println(ins);
			/*
			 * ��Ҫ��������Ϣ������������޸�ʱӦע���װ
			 */
			while (true) {

				System.out.println("a.send message to chat(input a)  b.get the online usernames(input b)");
				System.out.println("----------------------------------");
				String operator = scanner.nextLine();
				String choice;
				// choice = Integer.parseInt(operator);
				choice = operator;

				/*
				 * choice a. get the public key �ڻ�ȡpublic key֮���ٽ��д�������Ĳ���
				 * 
				 * -->ѡ�������ˣ���ȡpublic key�� -->ѡ�񴫵ݵ���Կ�ķ�ʽ��������Կ����ȥ
				 * -->ȷ����Կ�Լ����ܵķ�ʽ�󣬴��ݹ�ȥ��Ҫ��������Ϣ
				 * 
				 * 6.11 �˴������ļ�������� �ļ�����HASH value�Ĵ��䲢����ȷ��
				 * 
				 * ����ѡ��Ϊ�� 1. �����û����Լ�����(���������ȷ��) ���ɳ�ʼ���û��Ĺ�����Կ�Լ�˽����Կ (1. RSA 2.
				 * DH) 2.
				 * ����ѡ���û��Ĳ���(ѡ��ǰ���ߵ��û�)��ȷ���û�����Ϣ֮���Զ���ȡ�û��Ĺ�����Կ(�Ƿ���Ҫ������ܵķ�ʽ��ȷ��) 3.
				 * ����ѡ������Ϣ��ѡ��(1. ������Ϣ����message 2. �ļ��Ĵ���(1.�ı� 2.ͼƬ)) 3.1
				 * ��ͨ��Ϣ����(1.Caesar 2. Playfair 3.DES 4.Blowfish) 3.1.1 ѡ����ܷ�ʽ
				 * 3.1.2 ������Կ 3.1.3 ���������Ϣ 3.2 �ļ�����(hash MD5���ܷ�ʽ��ͨ��hash
				 * value��ȷ��ʵ���ļ��Ĵ���ȷ��)�� 3.2.1 ������Կ 3.2.2 �ı��ļ�����(String����) 3.2.3
				 * ͼƬ�ļ��Ĵ���(Byte[]����)
				 * 
				 * ȫ�ֵı�����Ҫ��ֲ��ı������ã�������ɻ��� ÿ�ּ��ܵ��������һ��Ҫ��ȷ���
				 */
				switch (choice) {
				case "a":
					/*
					 * ���choice ѡ�����������ж�
					 */
					System.out.println("\ninput the user name you want to chat: ");
					String username = scanner.nextLine();
					pw.println("a" + ":" + username);
					String ciphertext = null;
					
					/*
					 * 
					 * �ڴ˴���������޸�֤���м��ܷ�ʽ�Ĳ���
					 * 
					 */
					
					
					
					/*
					 * ȷ��RSA˫����public
					 * key֮�󣬽����û�ѡ����м��ܵ÷�ʽ�����Ҷ��ڼ����е���Կ����RSA��ʽ�ļ��ܣ��Ӷ�ʵ��share the
					 * key
					 */
					System.out.println("please choose RSA or DH to pass the secret key: R.rsa  D.dh");
					enfor=scanner.nextLine();//��ȡ�����Կ�ļ��ܷ�ʽ�ķ�ʽ
					/*
					 * ���û�ѡ��ʹ��DH ���м���ʱ
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
						 * ȷ��RSA˫����public
						 * key֮�󣬽����û�ѡ����м��ܵ÷�ʽ�����Ҷ��ڼ����е���Կ����RSA��ʽ�ļ��ܣ��Ӷ�ʵ��share
						 * the key
						 */
						Thread.sleep(500);
						/*
						 * �˴��޸ģ��޸�Ϊ��ǰ�Ѿ���ȡ���˼��ܷ�ʽ��ֻ��������Կ����
						 */
						
						System.out.println(
								"please input the encript type you use :1. Caesar  2.Playfair  3.DES  4.BlowFish");
						String type = scanner.nextLine();// ��ȡ���ܵķ�ʽ
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
							 * ������Ϣ������
							 */
							Thread.sleep(500);// ��Ъͣ��
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
							// key=list.get(0);//��ȡ������Կ�����д���
							/**
							 * �˴���RSA ���ܽ��ܷ�ʽ����
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
							 * ������Ϣ������
							 */
							Thread.sleep(500);// ��Ъͣ��
							System.out.println("Please input your message ");
							String info = scanner.nextLine();
							ciphertext = info;
							ciphertext = PlayFair.encrypt(info, key);
						}
						/*
						 * DES: ����Ҫ�󣺳�ʼ����Կ����Ϊ8Ϊ(64bit)
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
							 * ������Ϣ������
							 */
							Thread.sleep(500);// ��Ъͣ��
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
							// key=list.get(0);//��ȡ������Կ�����д���
							String key = list.get(0);
							for (int i = 1; i < list.size(); i++) {
								key = key + "BENZ" + list.get(i);
							}
							pw.println(type + " " + key + " " + username);
							/*
							 * ������Ϣ������
							 */
							Thread.sleep(500);// ��Ъͣ��
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
					 * �˴������ļ��Ĵ���
					 */
					else if (infoType.equals("F")) {
						// Thread.sleep(500);
						System.out.println("please input the file type you want :T. txt  J.jpg/bmp");
						String type = scanner.nextLine();// ��ȡ���ܵķ�ʽ
						if (type.equals("T")) {
							/*
							 * �ı��ļ��Ĵ��䣺����˼· 1.��ȡ������ļ��� 2.�����ļ�����ȡ�ļ�����Ϣ
							 * 3.�����ļ���Ϣ����MD5��hash���� 3.1����hash
							 * value���ܳ�Ϊ��Կ(RSA��ʽ(Public key private key �ķ�ʽ))
							 * 4.������ܺ��hash value����һ���ͻ��� 5.����hash
							 * value+�ļ���Ϣ������һ�����յĿͻ��˽���ȷ�� 5.1�ͻ�����Ϣȷ�ϣ��ȶԼ��ܹ���hash
							 * value���н��ܲ��� 5.2���ܺ��hash value�Է��͹�����hash value
							 * ���жԱȣ����һ�¿���ȷ���ļ�����Ϣ����ȷ��
							 * 
							 * ���⣺�ڽ����ļ��Ķ�д����ʱ�Ƿ�ᵼ���̲߳�������
							 */
							System.out.println("please input the filename you want to transfer");
							String filename = scanner.nextLine();
							File file = new File(filename);
							FileInputStream fi = new FileInputStream(file);
							int ch = 0;
							String info = new String();
							while ((ch = fi.read()) != -1) {
								info += (char) ch; // info����洢��Ϊ�ļ��ڵ���Ϣ
							}

							fi.close();
							MD5 md = new MD5();
							String hashValue = md.digest(info);// 1.��ȡhash value
							ArrayList<String> list = new ArrayList<>();
							list = RSA.dealEnMes(hashValue, Pkey);
							String keys = list.get(0);
							/*
							 * 14��09 �Ѿ�ʵ���˼��ܺ��hash
							 * value�Ĵ��䣬����ֱ��������˵����д���������⣬��������Դû�йر�
							 */
							for (int i = 1; i < list.size(); i++) {
								keys = keys + "BENZ" + list.get(i);// 2.����RSA���ܺ����Ϣ
							}
							System.out.println("the hash value is "+hashValue);
							pw.println("H" + " " + keys + " " + username);// 3.���Ƚ����ܹ�����Ϣ���͸������������ͻ���
							/*
							 * �˴�˼�������ܵ�hash value��hash value + txt
							 * ��Ϣ�Ƿ���Ҫ�ֿ����գ�֮���ٽ��жԱ�
							 */
							Thread.sleep(500);// ��Ъͣ��

							System.out.println("please enter to confirm the file: " + filename);
							String enter = scanner.nextLine();
							String infoTxt = new String();
							
							/**
							 * ���з� ��\n �س����з���\r\n
							 */
							info = info.replace("\r\n", "/*1111*/");// �ı���Ϣȥ���س����з�
							infoTxt = hashValue + "BENZ" + info;// 4. hash value
																// ���ı���Ϣ֮����BENZ�ַ������зָ�
							/**
							 * �Ѿ�������Ϣ
							 * 79CFEB94595DE33B3326C06AB1C7DBDABENZabcd123
							 * 
							 * ���ǻ��ǳ��ֵ��ߵ������ 1.�п������ļ���ȡ��Դ���߳�֮��ĳ�ͻ 2.�п�����Ϣ����Ĵ���
							 * 3.�������˳��ֻ�������������
							 * 
							 * �����������������Ϣ���Ƿ��������Ϣ
							 */
							
							pw.println("T" + " " + infoTxt + " " + username);// 5.
																				// ��T
																				// ��ͷ����hash��Ϣ+�ı���Ϣ
							Thread.sleep(500);
						}

						/*
						 * ͼƬ�ļ��Ĵ���ʵ��:����ʵ��Ϊ�����ֽڴ����ʵ��
						 *  
						 * ͼƬ�������̣�1.����������ļ��������ļ���ȡhash value 
						 *              2. ��hash value���ܽ��з���
						 *              3.��δ���ܵ�hash value������string ��ʽ���͸��ͻ�
						 *              4.ȷ����Ϣ����string��ʽ������ɣ�ͼƬ�������ֽ���ʽ���з���
						 *              
						 * ���Ҫ�㣺1.�ֿ�ʵ�֣��Ȱ�hash value��Ϣ���ܲ�����
						 *           2.��hash value����
						 *              (���������������ı��ļ��Ĵ�����ʽ)
						 *           3.����������ʵ��֮�󵥶�����ͼƬ�ķ���
						 */
						else if (type.equals("J")) {
							System.out.println("please input the filename you want to transfer");
							String filename = scanner.nextLine();
							File file = new File(filename);
							FileInputStream fi = new FileInputStream(file);
							
							//A. ��hash value���м��ܲ�����
							int ch = 0;
							String info = new String();
							while ((ch = fi.read()) != -1) {
								info += (char) ch; // info����洢��Ϊ�ļ��ڵ���Ϣ 
								                   //����ͼƬ�ĸ�ʽ��string ��Ϣ������Ϣֻ����ΪMD5���ܲ�������string������ʵ������
							}
							fi.close();//�˴��Ĺرղ�����Ҫ��һ������
							MD5 md = new MD5();
							String hashValue = md.digest(info);// 1.��ȡhash value
							ArrayList<String> list = new ArrayList<>();
							list = RSA.dealEnMes(hashValue, Pkey);
							String keys = list.get(0);
							/*
							 * 14��09 �Ѿ�ʵ���˼��ܺ��hash
							 * value�Ĵ��䣬����ֱ��������˵����д���������⣬��������Դû�йر�
							 */
							for (int i = 1; i < list.size(); i++) {
								keys = keys + "BENZ" + list.get(i);// 2.����RSA���ܺ����Ϣ
							}
							
							System.out.println("the hash value is "+hashValue);
							pw.println("H" + " " + keys + " " + username);// 3.���Ƚ����ܹ�����Ϣ���͸������������ͻ���
							Thread.sleep(500);
							int fileLen = 0;
							/**
							 * �˴�˼�����Ƿ����ļ�̫�����ļ��Ķ�ȡ��С�޷�ʵϰ�����Ķ�ȡ
							 */
							fileLen = (int) file.length();
							System.out.println(fileLen);
							//B.����δ���ܵ�hash value����(��string��ʽ)
							pw.println("J" + " " + hashValue + " " + username+" "+fileLen);
							Thread.sleep(500);
							
						
							//C. ͼƬ�ļ��Ĵ���
							//����ΪͼƬ�����ʽ
							fi = new FileInputStream(file);
							
							
//							int fileLen = fi.available();//��ȡ���ļ��Ĵ�С
							       
							/*
							 * �˴�ͼƬ������޸ģ�
							 *  �ȴ����ȥͼƬ��С��֮���ٴ����ļ�
							 */
							byte[] buf = new byte[fileLen];
							fi.read(buf);
							
							OutputStream out = clientSocket.getOutputStream();//��ȡ������ֽڴ��䷽ʽ
							/*
							 * 14:35 ͼƬ�����������ǳ��� �˶�����Ϣ�������������˵Ĵ��������Ҫ���
							 */
							out.write(buf,0,fileLen);//�ֽڴ���
							fi.close();
							/**
							 * 1.���������������ֻҪһ�߹رգ��������Լ�socket������������
							 * 2.��õ������ǣ�Ҫ�ر���һ��رգ�
							 */
//							out.close();
							
							
						}
					}

					break;
				case "b": // �����������֧ʵ�ֻ�ȡ��ǰ��������
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
			System.out.println("client starts successfully...��");
			System.out.println("\nPlease input your username��");
			name = scan.nextLine();
			if (name.trim().equals("")) {
				System.out.println("the name must not be empty ");
			} else {
				pw.println(name);
				Thread.sleep(500);
				String pass = br.readLine();
				if (pass != null && (!pass.equals("OK"))) {
					System.out.println("name is already occupied. Please re-enter��");
				} else {
					System.out.println("name ��" + name + "��   is created successfully");

					break;
				}
			}
		}
	}

	// The loop reads the messages sent by the server and outputs them to the
	// console on the client side
	/**
	 * ������Ϣ��Ϊ���㣬ֻ���ж�����Ϣ�ø�ʽ��Ȼ����д�����
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
						DHsecret=DH.DHB(dhKey);//��ȡ��ǰ����Կ
					}
					/**
					 * �˴�ƥ��������⣬startwith������ʹ�����⣺��ȡ��ѵ
					 */
					/*
					 * ����RSA���ܺ��hash value�Ľ��ܴ���
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
						 * �˴���ȡ���ļ��ܺ��hash value ����֮����ȷ
						 */
						System.out.println("[Encript form] Hash  [hash value] " + hashVal);
					}
					// TextFile
					/*
					 * text file :hash value+BENZ+info �˴���Ϣû����ʾ��
					 * 1.�ȼ������Ϣ������ǰһ���������˵Ĵ��� 2.������ȫ�ֱ����Ĵ���
					 */
					else if (msgString.startsWith("TextFile")) {

						String info[] = msgString.split(" ");

						String[] keys = info[1].split("BENZ");
						// System.out.println(info[1]+" "+hashVal);
						String hash = keys[0];// hash value
						String inf = keys[1];// text message
						inf = inf.replace("/*1111*/", "\r\n");
						/*
						 * �����ܺ��hash value �봫�����hash valueһ��
						 * ����ȷ���ı���Ϣһ������˿��Դ����ļ�������Ϣд��
						 */

						if (hash.equals(hashVal)) {
							/*
							 * �˴��ļ���д�����⣺�´�����д�뵽�ļ���д��Դ���Ƿ�ᵼ�µ�ǰ�̳߳���
							 */
							File file = new File("2.txt");

							// if file doesnt exists, then create it
							if (!file.exists()) {
								file.createNewFile();
							}

							FileWriter fw = new FileWriter(file.getAbsoluteFile());
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write(inf);
							bw.close();// �ر���Դ�Ƿ���룿
							fw.close();

						}
						/*
						 * hash value ��һ�� �ı���Ϣ����
						 */
						else {
							System.out.println("The two hash values are different!!");
						}
					}
//					JPGFile
					/*
					 * JPGFile�ļ��Ĵ������
					 *  ���̣�1.��ȡδ���ܵ�hash value ��Ϣ
					 *        2.����ܵ�hash value ���ܺ����Ϣ���жԱ�ȷ��
					 *        3.ͼƬ�ֽڵĻ�ȡ(����+����ֽ�)
					 */
					else if (msgString.startsWith("JPGFile")) {

						String info[] = msgString.split(" ");

						String hash = info[1];// hash value
						/*
						 * �����ܺ��hash value �봫�����hash valueһ��
						 *   
						 *   ��hash value ȷ����ȷ�󣬿��Խ���ͼƬ�ļ����ֽڽ����봴�����
						 */
						if (hash.equals(hashVal)) {
							/*
							 * �˴��ļ���д�����⣺�´�����д�뵽�ļ���д��Դ���Ƿ�ᵼ�µ�ǰ�̳߳���
							 *   ͼƬ�ֽ�û��д���ȥ
							 */
							File file = new File("2.JPG");

							System.out.println("JPG file creates");
							// if file doesnt exists, then create it
							if (!file.exists()) {
								file.createNewFile();
							}

							/*
							 * �������Ѿ�ʵ�ִ���hash������Ϣ֮�󣬽���ͼƬ��Ϣ�Ļ�ȡ�Լ���������һ���ͻ���
							 */
							InputStream in = clientSocket.getInputStream();
							//����ͼƬ�ֽ���
							FileOutputStream fos = new FileOutputStream("2.JPG");
							byte[] buf = new byte[1024];
							int len = 0;
							//���ֽ�����дͼƬ����
							while ((len = in.read(buf)) != -1)
							{
							fos.write(buf,0,len);//ͼƬ��Ϣд����server.JPG֮��
							}
							//��ȡ�������׼�����ͻ��˷�����Ϣ
							//�ر���Դ
							fos.close();
//							in.close();
						}
						/*
						 * hash value ��һ�� �ı���Ϣ����
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
					 * Caesar ������Ϣ
					 * 
					 * ���ܺ����Ϣ��ʾ�������⣺��ʾ����Ϣȱ�ٻ�������
					 */
					else if (msgString.startsWith("MessageCaesar")) {
						// System.out.println(msgString);
						String info[] = msgString.split(" ");
						String mes = msgString.replaceAll(info[0] + " ", "");
						;
						// System.out.println("mes: "+mes);
						String plain = Caesar.CaeInfo(mes, -K);
						System.out.println("Decrypted ciphertext��" + plain);
					}
					/*
					 * Playfair ������Ϣ ���ܺ����Ϣ�������⣬�޷���ȫ��ʾ ԭ������Ϊ��Ϣ�ڼ��ܹ�������ӵĿո������µ�����
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
						System.out.println("Decrypted ciphertext��" + plain);
					}
					/*
					 * DES ������Ϣ
					 */
					else if (msgString.startsWith("MessageDES")) {
						String info[] = msgString.split(" ");
						String mes = msgString.replaceAll(info[0] + " ", "");
						String plaintext = Des.encrypt(mes, systemmetic_key, "decrypt");
						plaintext = plaintext.replace("*", "");
						// System.out.println(systemmetic_key);
						System.out.println("Decrypted ciphertext��" + plaintext);
					}
					/*
					 * IDEA ������Ϣ : �˴����ܳ������⣬���ڼ��ܺ����Ϣ�޷�ʵ�������Ľ���
					 */
					else if (msgString.startsWith("MessageBlowFish")) {
						String info[] = msgString.split(" ");
						String mes = msgString.replaceAll(info[0] + " ", "");
						Blowfish bf = new Blowfish(BF_key);
						// System.out.println("ENMES: "+mes);
						String plain = bf.decryptString(mes);
						System.out.println("Decrypted ciphertext��" + plain);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}