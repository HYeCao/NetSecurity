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
	 * 此处为字节流的输入，应该会在图片的传输中进行使用(图片的传输操作应该参照之前实现的UDP/TCP的传输过程)
	 */
	private static Map<String, OutputStream> storeOutInfo;
	private static Map<String, String> storeCerKey;
	private String secretKey;
	
	public Chat_Server() {
		try {
			/*
			 * 切记创建变量时要进行初始化
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
//				
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
				OutputStream out=socket.getOutputStream();
				
				/*
				 * Store the customer nickname and what it says in a HashMap of
				 * the Shared collection
				 */
				name = getName();
				System.out.println("\n" + name + " ->connection succeeded！ ");
				
				putOutIn(name, out);//空指针报错
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
				String st=bReader.readLine();//public key+" "+alg form+" "+hash form+" "+time
				System.out.println(st);
				putCerKeyIn(name, st);
				// MsgString is a message sent by the client
				String k = null;
				while ((msgString = bReader.readLine()) != null) {
					
//					if(msgString.split(" ").length>=2){
					/*
					 * 获取public key
					 */
					if (msgString.startsWith("a")) {
//						System.out.println(msgString);
						String[] str = msgString.split(":");
						String theName = str[1];
						k = "1";
						System.out.println(name + " is chatting with " + theName);
						sendCerKeyToSomeone(name, "1", theName);
						/*
						 * 传递密钥是出现错误，因为当前设定的是在传
						 */
//						sendFormToSomeone(theName, k, name);
						
					}
					/*
					 * 实现讲所有在线人员的信息进行传输给指定的人员
					 */
					else if (msgString.startsWith("b")) {
//						System.out.println(msgString);
						sendOnlineToSomeone(name);
						
					}
					/**
					 * 文件传输来的加密后的hash value信息
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
					 * 文件传输来的hash value + txt 文本信息
					 */
					else if (msgString.startsWith("T")) {
						k = "T";
						String[] str = msgString.split(" ");
						System.out.println(msgString);
						String theName = str[2];
						/*
						 * 14:20 当前无法到达此处，无法进行信息的处理
						 *  问题:客户端的输入问题，无法进行连续的两条输入信息的连续处理，需要进行客户手动的输入的操作，从而区分出两个不同的输入的操作分析
						 */
						String info=str[1];//此处的info为hash value+BENZ+去除回车之后的文本信息
						System.out.println(name+" is sending the Text info to "+theName);
//						sendSecretKeyToSomeone("Caesar",secretKey,theName);
						sendToSomeone(k, theName, info);
					}
					/**
					 * 文件传输来的hash value + jpg 图片信息
					 *  1. 由于图片的传输是按照字节形式进行传输 所以hash value与图片的字节信息需要分开传输
					 *  2. 先处理传过来的未加密的hash value 
					 *  3. 再处理字节形式的图片信息
					 */
					/*
					 * 此处对于接收的信息应该分为两部分：
					 *    1. 未加密的hash value 信息 J hashvalue username
					 *    2. 在1. 以string形式传输之后，需要紧接着进行图片文件的传输
					 */
					else if (msgString.startsWith("J")) {
						k = "J";
						String[] str = msgString.split(" ");
						String theName = str[2];
						String fileL=str[3];
						/*
						 * 14:20 当前无法到达此处，无法进行信息的处理
						 *  问题:客户端的输入问题，无法进行连续的两条输入信息的连续处理，需要进行客户手动的输入的操作，从而区分出两个不同的输入的操作分析
						 */
						String info=str[1];//此处的info为hash value的文本信息
						System.out.println(name+" is sending the JPG/BMP info to "+theName);
//						sendSecretKeyToSomeone("Caesar",secretKey,theName);
						sendToSomeone(k, theName, info);
						
						/**
						 * 以下传输代码出现错误：
						 *   1.传输的第一个客户端socket关闭所致
						 *   2.资源的重复开启所致
						 */
						/*
						 * 在上述已经实现传输hash基本信息之后，进行图片信息的获取以及传输至另一个客户端
						 */
						/*
						 *19:40传输问题，没有将数据填充进去，只是创建了文件 
						 */
						int filelen=Integer.valueOf(fileL);
						System.out.println(msgString);
						byte[] buf = new byte[filelen];//图片的字节大小已经读入

						//往字节流里写图片数据
						InputStream in = socket.getInputStream();
						in.read(buf);
						//获取输出流，准备给客户端发送消息
						//关闭资源
						
						OutputStream outs = storeOutInfo.get(theName);//获取传输的字节传输方式
					
						outs.write(buf,0,filelen);//字节传输
					
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
					/**
					 * 此处为将DS传送至客户端
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
					 * 获取当前在线的人的信息
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