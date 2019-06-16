package lab3_1_certificate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Chat_Server {

	private ServerSocket serverSocket;

	private ExecutorService exec;

	// The output stream of information that holds private conversations between users
	private static final int port = 9999;
	private Map<String, PrintWriter> storeInfo;
	/*
	 * �˴�Ϊ�ֽ��������룬Ӧ�û���ͼƬ�Ĵ����н���ʹ��(ͼƬ�Ĵ������Ӧ�ò���֮ǰʵ�ֵ�UDP/TCP�Ĵ������)
	 */
	private static Map<String, OutputStream> storeOutInfo;
	private static Map<String, String> storeCerKey;
	private String secretKey;
	
	public Chat_Server() {
		try {
			/*
			 * �мǴ�������ʱҪ���г�ʼ��
			 */
			serverSocket = new ServerSocket(port);
			storeInfo = new HashMap<String, PrintWriter>();
			storeCerKey=new HashMap<String,String>();
			storeOutInfo=new HashMap<String, OutputStream>();
			exec = Executors.newCachedThreadPool();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send information to all clients
	 * 
	 * @param message
	 */
	private synchronized void sendToAll(String message) {
		for (PrintWriter out : storeInfo.values()) {
			out.println(message);
		}
	}
	/// Storing the client information in the collection as a Map
	private void putIn(String key, PrintWriter value) {
		synchronized (this) {
			storeInfo.put(key, value);
		}
	}
	private void putOutIn(String key, OutputStream value) {
		synchronized (this) {
			storeOutInfo.put(key, value);
		}
	}
	/**
	 * put key in
	 * @param key
	 * @param value
	 */
	private void putCerKeyIn(String key, String value) {
		synchronized (this) {
			storeCerKey.put(key, value);
		}
	}
	// Deletes a given output stream from a Shared collection
	private synchronized void remove(String key) {
		storeInfo.remove(key);
		System.out.println(key + " ->quit��");
	}



	/**
	 * Send cipher information to the specific client
	 * 
	 * @param name
	 * @param form
	 * @param sender
	 */
	private synchronized void sendFormToSomeone(String name, String form, String sender) {
		PrintWriter pw = storeInfo.get(name);
		if (pw != null)
			pw.println(form + ":" + sender);
	}

	/**
	 * 
	 * @param name
	 * @param form
	 * @param sender
	 */
	private synchronized void sendCerKeyToSomeone(String name, String form, String sender) {
		PrintWriter pw1 = storeInfo.get(name);
		PrintWriter pw2=storeInfo.get(sender);
		String key1=storeCerKey.get(name);
		String key2=storeCerKey.get(sender);
//		System.out.println(name+" "+key1);
//		System.out.println(sender+" "+key2);
		if (pw1 != null)
			pw1.println("[Certificate]"  +sender+" "+ key2);
		if (pw2 != null)
			pw2.println("[Certificate]"  +name+" "+ key1);
	}
	/**
	 * ����Կ������û�
	 * @param name
	 * @param form
	 * @param sender
	 */
	private synchronized void sendSecretKeyToSomeone(String form, String secretKey, String sender) {
		PrintWriter pw1 = storeInfo.get(sender);
//		System.out.println(name+" "+key1);
//		System.out.println(sender+" "+key2);
		if (pw1 != null)
			pw1.println( form +" "+"[SecretKey]"+" "+ secretKey);
	}
	/**
	 * 
	 * @param form
	 * @param secretKey
	 * @param sender
	 */
	private synchronized void sendOnlineToSomeone(String receiver) {
		PrintWriter pw1 = storeInfo.get(receiver);
//		storeInfo
		String names=new String();
		for (String name : storeInfo.keySet()) {
//			out.println(message);
			names +=" "+name;
		}
//		System.out.println(names);
		if (pw1 != null)
			pw1.println( "[OnlineUsers:]"+" "+ names);
	}
	/**
	 * Send information to the specific client
	 * 
	 * @param name
	 * @param message
	 */
	private synchronized void sendToSomeone(String form,String name, String message) {
//		System.out.println(form+" "+name);
		PrintWriter pw = storeInfo.get(name); // Take out the corresponding
												// client chat information and
												// send it as private chat
												// content
		if (pw != null){
			if(form.equals("1")){
				pw.println("MessageCaesar"+" "+message);
			}
			else if(form.equals("2")){
				pw.println("MessagePlayFair"+" "+message);
			}
			else if(form.equals("3")){
				pw.println("MessageDES"+" "+message);
			}
			else if(form.equals("4")){
				pw.println("MessageBlowFish"+" "+message);
			}
			else if(form.equals("T")){
				pw.println("TextFile"+" "+message);
			}else if(form.equals("J")){
				pw.println("JPGFile"+" "+message);
			}
			else if(form.equals("DS")){
				pw.println("DS"+" "+message);
			}
			
		}
	}

	/**
	 * �����������Ĳ���
	 */
	public void start() {
		System.out.println("Server is starting successfully ,Waiting for connection .");
		try {
			while (true) {

				Socket socket = serverSocket.accept();

				// Gets the client's IP address
				InetAddress address = socket.getInetAddress();
				/*
				 * Start a thread that handles client requests so that the next
				 * client connection can be monitored again
				 */
				exec.execute(new ListenrC(socket)); // Threads are
															// allocated through
															// a thread pool
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The line is used to handle a given client message, loop each string sent
	 * by the client, and output to the console
	 */
	class ListenrC implements Runnable {

		private Socket socket;
		private String name;

		public ListenrC(Socket socket) {
			this.socket = socket;
		}

		// Create an inner class to get a nickname
		private String getName() throws Exception {
			try {
				// The input stream on the server reads the name output stream
				// sent by the client
				BufferedReader bReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				// The server sends the result of name validation to the client
				// through its own output stream
				PrintWriter ipw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

				// Read the username from the client
				while (true) {
					String nameString = bReader.readLine();
					if ((nameString.trim().length() == 0) || storeInfo.containsKey(nameString)) {
						ipw.println("FAIL");
					} else {
						ipw.println("OK");

						return nameString;
					}
				}
			} catch (Exception e) {
				throw e;
			}
		}

		@Override
		public void run() {
			try {
				/*
				 * Gets the client's output stream through the client's Socket
				 * to send the message to the client
				 */
//				
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
				OutputStream out=socket.getOutputStream();
				
				/*
				 * Store the customer nickname and what it says in a HashMap of
				 * the Shared collection
				 */
				name = getName();
				System.out.println("\n" + name + " ->connection succeeded�� ");
				
				putOutIn(name, out);//��ָ�뱨��
				putIn(name, pw);
				
				Thread.sleep(100);

				// The server notifies all clients that a user is online
				sendToAll("[SystemMessage] ��" + name + "��is online");

				/*
				 * Gets the input stream through the client Socket to read the
				 * information sent by the client
				 */
				BufferedReader bReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				String msgString = null;
				String st=bReader.readLine();//public key+" "+alg form+" "+hash form+" "+time
				System.out.println(st);
				putCerKeyIn(name, st);
				// MsgString is a message sent by the client
				String k = null;
				while ((msgString = bReader.readLine()) != null) {
					
//					if(msgString.split(" ").length>=2){
					/*
					 * ��ȡpublic key
					 */
					if (msgString.startsWith("a")) {
//						System.out.println(msgString);
						String[] str = msgString.split(":");
						String theName = str[1];
						k = "1";
						System.out.println(name + " is chatting with " + theName);
						sendCerKeyToSomeone(name, "1", theName);
						/*
						 * ������Կ�ǳ��ִ�����Ϊ��ǰ�趨�����ڴ�
						 */
//						sendFormToSomeone(theName, k, name);
						
					}
					/*
					 * ʵ�ֽ�����������Ա����Ϣ���д����ָ������Ա
					 */
					else if (msgString.startsWith("b")) {
//						System.out.println(msgString);
						sendOnlineToSomeone(name);
						
					}
					/**
					 * �ļ��������ļ��ܺ��hash value��Ϣ
					 */
					else if (msgString.startsWith("H")) {
						k = "H";
						String[] str = msgString.split(" ");
						String theName = str[2];
						String secretKey=str[1];
//						System.out.println(name + " is chatting with " + theName);
						System.out.println(name+" is sending the file hash key to "+theName);
//						sendFormToSomeone(theName, k, name);
						sendSecretKeyToSomeone("Hash",secretKey,theName);
					}
					/**
					 * �ļ���������hash value + txt �ı���Ϣ
					 */
					else if (msgString.startsWith("T")) {
						k = "T";
						String[] str = msgString.split(" ");
						System.out.println(msgString);
						String theName = str[2];
						/*
						 * 14:20 ��ǰ�޷�����˴����޷�������Ϣ�Ĵ���
						 *  ����:�ͻ��˵��������⣬�޷���������������������Ϣ������������Ҫ���пͻ��ֶ�������Ĳ������Ӷ����ֳ�������ͬ������Ĳ�������
						 */
						String info=str[1];//�˴���infoΪhash value+BENZ+ȥ���س�֮����ı���Ϣ
						System.out.println(name+" is sending the Text info to "+theName);
//						sendSecretKeyToSomeone("Caesar",secretKey,theName);
						sendToSomeone(k, theName, info);
					}
					/**
					 * �ļ���������hash value + jpg ͼƬ��Ϣ
					 *  1. ����ͼƬ�Ĵ����ǰ����ֽ���ʽ���д��� ����hash value��ͼƬ���ֽ���Ϣ��Ҫ�ֿ�����
					 *  2. �ȴ���������δ���ܵ�hash value 
					 *  3. �ٴ����ֽ���ʽ��ͼƬ��Ϣ
					 */
					/*
					 * �˴����ڽ��յ���ϢӦ�÷�Ϊ�����֣�
					 *    1. δ���ܵ�hash value ��Ϣ J hashvalue username
					 *    2. ��1. ��string��ʽ����֮����Ҫ�����Ž���ͼƬ�ļ��Ĵ���
					 */
					else if (msgString.startsWith("J")) {
						k = "J";
						String[] str = msgString.split(" ");
						String theName = str[2];
						String fileL=str[3];
						/*
						 * 14:20 ��ǰ�޷�����˴����޷�������Ϣ�Ĵ���
						 *  ����:�ͻ��˵��������⣬�޷���������������������Ϣ������������Ҫ���пͻ��ֶ�������Ĳ������Ӷ����ֳ�������ͬ������Ĳ�������
						 */
						String info=str[1];//�˴���infoΪhash value���ı���Ϣ
						System.out.println(name+" is sending the JPG/BMP info to "+theName);
//						sendSecretKeyToSomeone("Caesar",secretKey,theName);
						sendToSomeone(k, theName, info);
						
						/**
						 * ���´��������ִ���
						 *   1.����ĵ�һ���ͻ���socket�ر�����
						 *   2.��Դ���ظ���������
						 */
						/*
						 * �������Ѿ�ʵ�ִ���hash������Ϣ֮�󣬽���ͼƬ��Ϣ�Ļ�ȡ�Լ���������һ���ͻ���
						 */
						/*
						 *19:40�������⣬û�н���������ȥ��ֻ�Ǵ������ļ� 
						 */
						int filelen=Integer.valueOf(fileL);
						System.out.println(msgString);
						byte[] buf = new byte[filelen];//ͼƬ���ֽڴ�С�Ѿ�����

						//���ֽ�����дͼƬ����
						InputStream in = socket.getInputStream();
						in.read(buf);
						//��ȡ�������׼�����ͻ��˷�����Ϣ
						//�ر���Դ
						
						OutputStream outs = storeOutInfo.get(theName);//��ȡ������ֽڴ��䷽ʽ
					
						outs.write(buf,0,filelen);//�ֽڴ���
					
					}
					/*
					 * �˴���˼����������һ�����룺��һ�����û�ѡ������ܵķ�ʽʱ���Ѿ�ȷ���������Ĵ��䷽ʽ��֮�����Կ����ֻ����Ҫ���з��ͼ���
					 * 
					 */
					/*
					 * Caesar key share:" 1 secret key "
					 */
					else if (msgString.startsWith("1")) {
						k = "1";
						String[] str = msgString.split(" ");
						String theName = str[2];
						String secretKey=str[1];
//						System.out.println(name + " is chatting with " + theName);
						System.out.println(name+" is sending the Caesar key to "+theName);
//						sendFormToSomeone(theName, k, name);
						sendSecretKeyToSomeone("Caesar",secretKey,theName);
					}
					/*
					 * PlayFair
					 */
					else if (msgString.startsWith("2")) {
						k = "2";
						String[] str = msgString.split(" ");
						String theName = str[2];
						String secretKey=str[1];
						
//						System.out.println(name + " is chatting with " + theName);
						System.out.println(name+" is sending the playfair key to "+theName);
//						sendFormToSomeone(theName, k, name);
						sendSecretKeyToSomeone("PlayFair",secretKey,theName);
					}
					/*
					 * DES
					 */
					else if (msgString.startsWith("3")) {
						k = "3";
						String[] str = msgString.split(" ");
						String theName = str[2];
						String secretKey=str[1];
//						System.out.println(name + " is chatting with " + theName);
						System.out.println(name+" is sending the DES key to "+theName);
//						sendFormToSomeone(theName, k, name);
						sendSecretKeyToSomeone("DES",secretKey,theName);
					}
					/*
					 * BlowFish
					 */
					else if (msgString.startsWith("4")) {
						k = "4";
						String[] str = msgString.split(" ");
						String theName = str[2];
						String secretKey=str[1];
//						System.out.println(name + " is chatting with " + theName);
						System.out.println(name+" is sending the BlowFish key to "+theName);
						sendSecretKeyToSomeone("BlowFish",secretKey,theName);
					}
					/*
					 * �˴�������Ϣ�Ĵ���˼·�������б���ܷ�ʽ ��ν����ܷ�ʽ�Լ���Ϣ���ݹ�ȥ ����ڽ��յ��û���ȷ�ϼ��ܷ�ʽ�����н���
					 * 
					 * �˴��������ʣ�����secret key�������Ƿ���Ҫ�����û������б��滹��ֻ�豣��һ�����ɣ���
					 */
					else if (msgString.startsWith("@")) {
						int index = msgString.indexOf(":");
						if (index >= 0) {
							
							String theName = msgString.substring(1, index);
							System.out.println(name+" is sending the message to "+theName);
							String info = msgString.substring(index + ":".length(), msgString.length());

							sendToSomeone(k,theName, info);
							continue;
						}
					}
					/**
					 * �˴�Ϊ��DS�������ͻ���
					 */
					else if (msgString.startsWith("DS")) {
//						System.out.println(msgString);
					   String kk="DS";
					   String[] inf=msgString.split(" ");
					   String theName=inf[1];
					   String info=inf[2];
					   System.out.println(name+" is sending the ds message to "+theName);
					   sendToSomeone(kk,theName, info);
				 }
					
					/*
					 * ��ȡ��ǰ���ߵ��˵���Ϣ
					 */
					else if(msgString.startsWith("b")){
						
					}

//					}
				}

			} catch (Exception e) {
				 e.printStackTrace();
			} finally {
				remove(name);
				// Notifies all clients that client x is offline
				sendToAll("[SystemMessage] " + name + " is offline");

				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		
	}

	public static void main(String[] args) {
		Chat_Server server = new Chat_Server();
		server.start();
	}
}