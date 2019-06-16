package rsa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
	
	private Map<String, String> storeKey;
	private String secretKey;
	
	public Chat_Server() {
		try {
			/*
			 * 切记创建变量时要进行初始化
			 */
			serverSocket = new ServerSocket(port);
			storeInfo = new HashMap<String, PrintWriter>();
			storeKey=new HashMap<String,String>();
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
	/**
	 * put key in
	 * @param key
	 * @param value
	 */
	private void putKeyIn(String key, String value) {
		synchronized (this) {
			storeKey.put(key, value);
		}
	}
	// Deletes a given output stream from a Shared collection
	private synchronized void remove(String key) {
		storeInfo.remove(key);
		System.out.println(key + " ->quit！");
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
	private synchronized void sendKeyToSomeone(String name, String form, String sender) {
		PrintWriter pw1 = storeInfo.get(name);
		PrintWriter pw2=storeInfo.get(sender);
		String key1=storeKey.get(name);
		String key2=storeKey.get(sender);
//		System.out.println(name+" "+key1);
//		System.out.println(sender+" "+key2);
		if (pw1 != null)
			pw1.println("[PublicKey]"  +sender+" "+ key2);
		if (pw2 != null)
			pw2.println("[PublicKey]"  +name+" "+ key1);
	}
	/**
	 * 将密钥传输给用户
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
			
		}
	}

	/**
	 * 服务器启动的操作
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
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

				/*
				 * Store the customer nickname and what it says in a HashMap of
				 * the Shared collection
				 */
				name = getName();
				System.out.println("\n" + name + " ->connection succeeded！ ");
				putIn(name, pw);
				Thread.sleep(100);

				// The server notifies all clients that a user is online
				sendToAll("[SystemMessage] “" + name + "”is online");

				/*
				 * Gets the input stream through the client Socket to read the
				 * information sent by the client
				 */
				BufferedReader bReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				String msgString = null;
				String st=bReader.readLine();//public key
				System.out.println(st);
				putKeyIn(name, st);
				// MsgString is a message sent by the client
				String k = null;
				while ((msgString = bReader.readLine()) != null) {

					
					/*
					 * 获取public key
					 */
					if (msgString.startsWith("a")) {
//						System.out.println(msgString);
						String[] str = msgString.split(":");
						String theName = str[1];
						k = "1";
						System.out.println(name + " is chatting with " + theName);
						sendKeyToSomeone(name, "1", theName);
						/*
						 * 传递密钥是出现错误，因为当前设定的是在传
						 */
//						sendFormToSomeone(theName, k, name);
						
						
						
					}
					/*
					 * 此处的思考：产生的一个构想：在一方的用户选择传输加密的方式时便已经确定的两方的传输方式，之后的密钥传输只是需要进行发送即可
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
					 * 此处对于信息的处理：思路：首先判别加密方式 其次将加密方式以及信息传递过去 最后在接收的用户处确认加密方式并进行解密
					 * 
					 * 此处存在疑问，对于secret key的设置是否需要依据用户名进行保存还是只需保存一个即可？？
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
					/*
					 * 获取当前在线的人的信息
					 */
					else if(msgString.startsWith("b")){
						
					}

				}

			} catch (Exception e) {
				// e.printStackTrace();
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