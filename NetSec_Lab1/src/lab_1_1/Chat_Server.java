package lab_1_1;

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
	

	public Chat_Server() {
		try {
			serverSocket = new ServerSocket(port);
			storeInfo = new HashMap<String, PrintWriter>();
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
	 * Send information to the specific client
	 * 
	 * @param name
	 * @param message
	 */
	private synchronized void sendToSomeone(String name, String message) {
		PrintWriter pw = storeInfo.get(name); // Take out the corresponding
												// client chat information and
												// send it as private chat
												// content
		if (pw != null)
			pw.println(message);

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

				// MsgString is a message sent by the client
				while ((msgString = bReader.readLine()) != null) {

					String k = null;
					/*
					 * Caesar cipher
					 */
					if (msgString.startsWith("1")) {
						String[] str = msgString.split(":");
						String theName = str[1];
						// System.out.println(theName);
						k = "1";
						System.out.println(name + " is chatting with " + theName);
						sendFormToSomeone(theName, k, name);
					}
					/*
					 * Playfair cipher
					 */
					else if (msgString.startsWith("2")) {
						k = "2";
						String[] str = msgString.split(":");
						String theName = str[1];
						System.out.println(name + " is chatting with " + theName);
						sendFormToSomeone(theName, k, name);
					}
					if (msgString.startsWith("@")) {
						int index = msgString.indexOf(":");
						if (index >= 0) {
							// To obtain a nickname
							String theName = msgString.substring(1, index);
							String info = msgString.substring(index + ":".length(), msgString.length());

							sendToSomeone(theName, info);
							continue;
						}
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