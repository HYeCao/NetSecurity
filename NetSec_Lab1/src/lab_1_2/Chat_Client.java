package lab_1_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Chat_Client {
	/*
	 * setting the socket and Other global variables
	 */
	private static Socket clientSocket;
	private static int K = 3;//Initially set the key to the Caesar password
	private static String key = "MYSECRETKEY";//Default play fair key
	private String name;

	private static final int port = 9999;
	private static final String serverIP = "127.0.0.1";

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
	 */
	public void start() {
		try {
			Scanner scanner = new Scanner(System.in);

			// The thread that receives the message sent from the server starts
			ExecutorService exec = Executors.newCachedThreadPool();
			exec.execute(new ListenrS());

			// Create an output stream to send messages to the server
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
			// Pass the public key

			while (true) {

				System.out.println("1.send message to chat(input 1)");
				System.out.println("----------------------------------");
				String operator = scanner.nextLine();
				int choice;
				choice = Integer.parseInt(operator);
				switch (choice) {
				case 1:
					System.out.println("\ninput the user name you want to chat: ");
					String username = scanner.nextLine();
					System.out.println("\ninput the cipher type you want : 1.Caesar 2.Playfair");
					String num = scanner.nextLine();
					System.out.println("\ninput the message you want to send:");
					String plain = scanner.nextLine();
					pw.println(num + ":" + username);
					String ciphertext = null;
					/*
					 * Caesar encrypted
					 */
					if (num.equals("1")) {
						Caesar caesar = new Caesar();

						ciphertext = caesar.CaeInfo(plain, K);// File information and key information pass
						System.out.println("Encrypted ciphertext£º" + ciphertext);
					}
					/*
					 * Playfair encrypted
					 */
					else if (num.equals("2")) {
						ciphertext = PlayFair.encrypt(plain, key);//File information and key information pass
						System.out.println("Encrypted ciphertext£º" + ciphertext);
					}
					Thread.sleep(500);
					/*
					 * replace the enter
					 */
					ciphertext = ciphertext.replace("\n", "/*1111*/");
					pw.println("@" + username + ":" + ciphertext);
					Thread.sleep(500);

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
			System.out.println("client starts successfully...£º");
			System.out.println("\nPlease input your username£º");
			name = scan.nextLine();
			if (name.trim().equals("")) {
				System.out.println("the name must not be empty ");
			} else {
				pw.println(name);
				Thread.sleep(500);
				String pass = br.readLine();
				if (pass != null && (!pass.equals("OK"))) {
					System.out.println("name is already occupied. Please re-enter£º");
				} else {
					System.out.println("name¡°" + name + "¡±   is created successfully");

					break;
				}
			}
		}
	}

	// The loop reads the messages sent by the server and outputs them to the
	// console on the client side
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
					/*
					 * Caesar decrypted
					 */
					else if (msgString.startsWith("1")) {
						/**
						 * deal the string
						 */
						String[] str = msgString.split(":");
						String sender = str[1];
						System.out.println(sender + " is sending a message to you by Caesar.");
						String info = br.readLine();
						Caesar caesar = new Caesar();

						String plain = caesar.CaeInfo(info, -K);// File information and key information are passed in
																 
						System.out.println("Decrypted ciphertext£º" + plain);
					}
					/*
					 * Playfair Decrypted
					 */
					else if (msgString.startsWith("2")) {
						String[] str = msgString.split(":");
						String sender = str[1];
						System.out.println(sender + " is sending a message to you by Playfair.");
						String info = br.readLine();

						String plain = PlayFair.decrypt(info, key);// File information and key information are passed in
																	
						System.out.println("Decrypted ciphertext£º" + plain);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}