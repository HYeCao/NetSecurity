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
 * 6.2:���û�֮��ķ����Լ����ܵ������Ѿ��������������Ҫ��Զ���û���ͬ���ߵĲ���������֤��ͬʱ׼��UI �Լ�DES�ȼ����㷨
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
	
	/* ��Կ�ֿ��洢�Ƿ����һЩ��
	 * 
	 * ���ڴ洢���ּ��ܷ�ʽ����Կ
	 */
	private String secretKey;
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
			pair = RSA_generateKey.generatorKey(12);// ������ʼ��private key �Լ� public key
			/*
			 * �˴����ܽ��ܳ������⣬��������û��Ĳ���Ӧ�ý��й�Կ�Լ�˽Կ�ķֿ����ܣ����RSA���ܽ��ܴ���										
			 */
			RSA.iniNum(pair);// ��ֵ������Ϊ��һ������Կ���ܴ�����׼��
			pw.println(pair.getPublicKey().toString());
			/*
			 * ��Ҫ��������Ϣ������������޸�ʱӦע���װ
			 */
			while (true) {

				System.out.println("a.send message to chat(input a)    b.get the online usernames(input b)");
				System.out.println("----------------------------------");
				String operator = scanner.nextLine();
				String choice;
//				choice = Integer.parseInt(operator);
				choice=operator;
				/*
				 * ���choice ѡ�����������ж�
				 */
				System.out.println("\ninput the user name you want to chat: ");
			    String username = scanner.nextLine();
					pw.println("a" + ":" + username );
					/*
					 * choice a. get the public key �ڻ�ȡpublic key֮���ٽ��д�������Ĳ���
					 * 
					 * -->ѡ�������ˣ���ȡpublic key��
					 *    -->ѡ�񴫵ݵ���Կ�ķ�ʽ��������Կ����ȥ
					 *       -->ȷ����Կ�Լ����ܵķ�ʽ�󣬴��ݹ�ȥ��Ҫ��������Ϣ
					 *       
					 *  6.11 �˴������ļ��������  �ļ�����HASH value�Ĵ��䲢����ȷ��
					 *  
					 *  ����ѡ��Ϊ��
					 *   1. �����û����Լ�����(���������ȷ��)  ���ɳ�ʼ���û��Ĺ�����Կ�Լ�˽����Կ (1. RSA 2. DH)
					 *   2. ����ѡ���û��Ĳ���(ѡ��ǰ���ߵ��û�)��ȷ���û�����Ϣ֮���Զ���ȡ�û��Ĺ�����Կ(�Ƿ���Ҫ������ܵķ�ʽ��ȷ��)
					 *   3. ����ѡ������Ϣ��ѡ��(1. ������Ϣ����message 2. �ļ��Ĵ���(1.�ı� 2.ͼƬ))
					 *     3.1 ��ͨ��Ϣ����(1.Caesar 2. Playfair 3.DES 4.Blowfish)
					 *       3.1.1 ѡ����ܷ�ʽ
					 *       3.1.2 ������Կ
					 *       3.1.3 ���������Ϣ
					 *     3.2 �ļ�����(hash MD5���ܷ�ʽ��ͨ��hash value��ȷ��ʵ���ļ��Ĵ���ȷ��)��
					 *       3.2.1 ������Կ
					 *       3.2.2 �ı��ļ�����(String����)
					 *       3.2.3 ͼƬ�ļ��Ĵ���(Byte[]����)
					 */
				switch (choice) {
				case "a":

					String ciphertext = null;
						/*
						 *ȷ��RSA˫����public key֮�󣬽����û�ѡ����м��ܵ÷�ʽ�����Ҷ��ڼ����е���Կ����RSA��ʽ�ļ��ܣ��Ӷ�ʵ��share the key 
						 */
					Thread.sleep(500);
					System.out.println("please input the encript info you want :M. Message F. file");
					String infoType=scanner.nextLine();
					if(infoType.equals("M")){
						Thread.sleep(500);
						System.out.println("please input the encript type you want :1. Caesar  2.Playfair  3.DES  4.BlowFish");
						String  type=scanner.nextLine();//��ȡ���ܵķ�ʽ
						/**
						 * �˴���Կ�Ĵ�����ִ���
						 *   ��Ը������ܷ�������Կ����Ҫ����RSA���ܲ����ſ��Խ��д���
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
							key=list.get(0);//��ȡ������Կ�����д���
							pw.println(type+" "+key+" "+username);
							/*
							 * ������Ϣ������
							 */
							Thread.sleep(500);//��Ъͣ��
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
//							key=list.get(0);//��ȡ������Կ�����д���
							/**
							 * �˴���RSA ���ܽ��ܷ�ʽ����
							 */
							System.out.println(RSA.dealDeMes(list));
							String keys=list.get(0);
							for(int i=1;i<list.size();i++){
								keys=keys+"BENZ"+list.get(i);
							}
							pw.println(type+" "+keys+" "+username);
							/*
							 * ������Ϣ������
							 */
							Thread.sleep(500);//��Ъͣ��
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
//							key=list.get(0);//��ȡ������Կ�����д���
							String keys=list.get(0);
							for(int i=1;i<list.size();i++){
								keys=keys+"BENZ"+list.get(i);
							}
							pw.println(type+" "+keys+" "+username);
							/*
							 * ������Ϣ������
							 */
							Thread.sleep(500);//��Ъͣ��
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
//							key=list.get(0);//��ȡ������Կ�����д���
							String key=list.get(0);
							for(int i=1;i<list.size();i++){
								key=key+"BENZ"+list.get(i);
							}
							pw.println(type+" "+key+" "+username);
							/*
							 * ������Ϣ������
							 */
							Thread.sleep(500);//��Ъͣ��
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
					 * �˴������ļ��Ĵ���
					 */
					else if(infoType.equals("F")){
						
					}
					
					
					break;
					
				case "b": //					�����������֧ʵ�ֻ�ȡ��ǰ��������
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
					 * �˴�ƥ��������⣬startwith������ʹ�����⣺��ȡ��ѵ
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
					 * Caesar ������Ϣ
					 * 
					 * ���ܺ����Ϣ��ʾ�������⣺��ʾ����Ϣȱ�ٻ�������
					 */
					else if (msgString.startsWith("MessageCaesar")) {
//						System.out.println(msgString);
						String info[] =msgString.split(" ");
						String mes=msgString.replaceAll(info[0]+" ", "");;
//						System.out.println("mes: "+mes);
						String plain = Caesar.CaeInfo(mes, -K);
						System.out.println("Decrypted ciphertext��" + plain);
					}
					/*
					 * Playfair ������Ϣ 
					 * ���ܺ����Ϣ�������⣬�޷���ȫ��ʾ
					 * ԭ������Ϊ��Ϣ�ڼ��ܹ�������ӵĿո������µ�����
					 */
					else if (msgString.startsWith("MessagePlayFair")) {
						String info[] =msgString.split(" ");
						String mes=msgString.replaceAll(info[0]+" ", "");
//						System.out.println(mes);
						String plain = PlayFair.decrypt(mes, key);// File information and key information are passed in
						System.out.println("Decrypted ciphertext��" + plain);
					}
					/*
					 * DES ������Ϣ
					 */
					else if (msgString.startsWith("MessageDES")) {
						String info[] =msgString.split(" ");
						String mes=msgString.replaceAll(info[0]+" ", "");
						String plaintext = Des.encrypt(mes,systemmetic_key, "decrypt");
						plaintext = plaintext.replace("*", "");
						System.out.println("Decrypted ciphertext��" + plaintext);
					}
					/*
					 * IDEA ������Ϣ :
					 * �˴����ܳ������⣬���ڼ��ܺ����Ϣ�޷�ʵ�������Ľ���
					 */
					else if (msgString.startsWith("MessageBlowFish")) {
						String info[] =msgString.split(" ");
						String mes=msgString.replaceAll(info[0]+" ", "");
					   Blowfish bf=new Blowfish(BF_key);
//					   System.out.println("ENMES: "+mes);
					   String plain=bf.decryptString(mes);
						System.out.println("Decrypted ciphertext��" + plain);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}