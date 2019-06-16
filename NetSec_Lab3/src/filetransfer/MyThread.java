package filetransfer;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;


/**
 * java澶氱嚎绋�
 * 
 * @author Cao Hongye
 *
 */
public class MyThread implements Runnable { // 璐熻矗涓庡崟涓鎴烽�氫俊鐨勭嚎绋�
	// 鍏ㄥ眬鍙橀噺鐨勫垵濮嬪畾涔�
	private Socket socket;
	private DatagramSocket Usocket;
	private String info;
	BufferedReader br;
	BufferedWriter bw;
	PrintWriter pw;
	File folder;
	private final String root = "D:/鏂板缓鏂囦欢澶�/鏂板缓鏂囦欢澶�/java";
	private String currentPath = root;
	static int UDPport = 2020;

	/**
	 * 鍑芥暟鍒濆瀹氫箟
	 * 
	 * @param socket
	 */
	public MyThread(Socket socket) {
		this.socket = socket;
		// this.info=info;
		folder = new File(root);
	}

	/***
	 * 杈撳叆杈撳嚭娴佺殑鍒濆鍖栧畾涔�
	 * 
	 * @throws IOException
	 */
	public void initStream() throws IOException { // 鍒濆鍖栬緭鍏ヨ緭鍑烘祦瀵硅薄鏂规硶
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		pw = new PrintWriter(bw, true);
	}

	/**
	 * 鍔熻兘锛歳un鏂规硶鐨勭紪鍐欙紝瀹炵幇璇诲彇瀹㈡埛绔俊鎭紝杩涜鐩稿簲鎿嶄綔锛屽苟杩斿洖鍏跺搴斾俊鎭� 鍏蜂綋鐨勫姛鑳藉疄鐜伴�氳繃璋冪敤鐩稿簲鍑芥暟瀹炵幇
	 */
	public void run() { // 鎵ц鐨勫唴瀹�
		try {
			initStream(); // 鍒濆鍖栬緭鍏ヨ緭鍑烘祦瀵硅薄
			boolean connection = true;

			String info1 = info;
			while (connection) {
				if ((info = br.readLine()) == null) {
					info = info1;
				}
				System.out.println(info);
				if (info.equals("bye")) { // 濡傛灉鐢ㄦ埛杈撳叆鈥渂ye鈥濆氨閫�鍑�
					break;
				} else if (info.equals("ls")) {
					ls();
				}

				else if (info.equals("cd..")) {
					cdback();
				} else {
					StringTokenizer stringTokenizer = new StringTokenizer(info, " ");
					String first = stringTokenizer.nextToken(); // 瑙ｆ瀽鍑虹涓�閮ㄥ垎

					if (first.equals("cd")) {
						String Path = stringTokenizer.nextToken(); // 瑙ｆ瀽鍑虹浜岄儴鍒�
						cd(Path);
					} else if (first.equals("get")) {

						String getPath = stringTokenizer.nextToken();
						// System.out.println("begin transfor\0");
						get(getPath);
					} else {
						pw.println("unknown cmd \n\0");// 杩斿洖鐢ㄦ埛鍙戦�佺殑娑堟伅
					}

				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != socket) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 缂栧啓cdback鏂规硶锛屽疄鐜拌繑鍥炰笂涓�鐩綍鐨勫姛鑳� 鍙傛暟锛氱┖ 杩斿洖鍊硷細绌� 瀹炵幇锛氶�氳繃淇敼鍏ㄥ眬鍙橀噺path鐨勫湴鍧�淇℃伅锛� 瀹炵幇淇敼褰撳墠鍦板潃骞惰繑鍥炰笂涓�绾�
	 * 寰呬慨鏀癸細闇�鍒ゅ埆鏄惁鍒拌揪鏍圭洰褰曪紝鍔犱竴涓牴鐩綍鐨勫垽鏂�
	 */

	private void cdback() { // 鍝嶅簲cd..
		if (currentPath.equals(root + "/") || currentPath.equals(root)) { // 濡傛灉鏄牴鐩綍锛屽垯鏃犳硶缁х画鍚戜笂
			pw.println("this is root catalog " + currentPath + "\n\0");
		} else { // 濡傛灉涓嶆槸鏍圭洰褰曪紝鍒欏悜涓婅繑鍥�
			String OldPath = currentPath;
			StringTokenizer stnr = new StringTokenizer(OldPath, "/"); // 鐢⊿tringTokenizer浠�/涓哄垎鐣屽璺緞杩涜鍒囧壊
			String Path = ""; // Path璁板綍涓婄骇鐩綍
			int countTokens = stnr.countTokens(); // 璁＄畻鍏辨湁鍑犱釜鍒囧壊缁撴灉
			for (int i = 0; i < countTokens - 1; i++) { // 淇濈暀闄ゅ幓鏈�鍚庝竴椤圭殑璺緞锛屽嵆鍙緱鍒板綋鍓嶇洰褰曠殑涓婂眰鐩綍
				Path = Path + stnr.nextToken() + "/";
			}
			currentPath = Path; // 鏇存柊褰撳墠鐩綍
			folder = new File(currentPath);// 鏇存柊褰撳墠鐩綍
			pw.println(currentPath + " > OK\n\0"); // 鍚戝鎴风鍙戦�佹垚鍔熻繑鍥炰笂灞傜洰褰曠殑淇℃伅

		}

	}

	/**
	 * 缂栧啓ls鏂规硶锛屽疄鐜版煡璇㈠嚭褰撳墠鐩綍鐨勬搷浣� 鍙傛暟锛氱┖ 杩斿洖鍊硷細绌�
	 * 瀹炵幇锛氶�氳繃璇诲彇鍏ㄥ眬鍙橀噺folder鐨勫湴鍧�淇℃伅锛屽疄鐜板綋鍓嶇洰褰曠殑寰幆骞惰繑鍥炲叾鐩稿簲鐨勪俊鎭粰瀹㈡埛绔�
	 */
	private void ls() {
		String allFile = new String();
		for (File listOfFile : folder.listFiles()) { // 鏂囦欢鐨勭洰褰曞惊鐜�
			if (listOfFile.isDirectory()) { // 鍒ゅ埆璇ヨ矾寰勬槸鍚︽纭紝鏄惁鏈夌洰褰�
				long size = getDirSize(listOfFile, 0); // 鑾峰彇姣忎釜鏂囦欢鐨勫ぇ灏�
				allFile += "<dir>  " + listOfFile.getName() + "  " + size / 1024 + "KB\n";// 瀛樺偍鑾峰彇鐨勬枃浠朵俊鎭笌allfile瀛楃涓蹭腑
			} else if (listOfFile.isFile()) {
				// 鍒ゆ柇璇ヨ矾寰勪笅鏄枃浠惰繕鏄洰褰�
				long size = listOfFile.length();
				allFile += "<File>  " + listOfFile.getName() + "  " + size / 1024 + "KB\n";
			}
		}
		// 娣诲姞鏍囪瘑绗︼紝浠ョ‘瀹氬鎴风杈撳嚭寰幆缁堟鏉′欢
		allFile += "\0";
		pw.println(allFile);// 杈撳叆淇℃伅娴佽嚦瀹㈡埛绔�

	}

	/**
	 ** 鍔熻兘锛氱紪鍐檆d鏂规硶锛屽疄鐜版煡鎵剧洰褰曠殑鍔熻兘 瀹炵幇锛氬浜庤緭鍏ョ殑鍦板潃淇℃伅棣栧厛鍒ゅ埆鏄惁鍦ㄥ綋鍓嶇洰褰曚笅锛屽湪鍒ゅ埆鍚庨�氳繃缁欏畾鐨勭洰褰曚俊鎭繘鍏ヨ鏂囦欢鐩綍涓�
	 * 锛堜慨鏀瑰綋鍓嶇殑鐩綍鍦板潃folder) 娉ㄦ剰锛� 1.鍒ゅ埆鎵�缁欐枃浠剁殑淇℃伅鏄惁閿欒鎴栬�呬笉瀛樺湪褰撳墠鐩綍涓� 2.鍒ゅ埆缁欏畾鍦板潃涓虹洰褰曟枃浠跺す杩樻槸鏂囦欢鐨勭被鍨�
	 * 3.淇敼褰撳墠鐨勭洰褰曞湴鍧�folder瀹炵幇鍦板潃鐨勪慨鏀癸紝鍛煎簲cd..杩斿洖涓婁竴绾х殑鍔熻兘
	 * 
	 * @param path:
	 *            鐢ㄦ埛杈撳叆鐨勬枃浠惰矾寰勪俊鎭�
	 * 
	 */
	private void cd(String path) {
		String allfile = new String();
		int temp = 0;
		for (File listFile : folder.listFiles()) { // 鏂囦欢鐨勭洰褰曞惊鐜�
			if (listFile.isDirectory()) { // 鍒ゅ埆璇ヨ矾寰勬槸鍚︽纭紝鏄惁鏈夌洰褰�
				if (listFile.getName().equals(path)) {
					/**
					 * 杩欓噷鍒ゆ柇鏈夌偣闂
					 */
					currentPath = currentPath + "/" + path;
					// pw.println(currentPath);
					folder = new File(currentPath);
					temp++;
					pw.println(path + " >" + " OK\n\0");
					break;
				} else if (listFile.isFile()) {
					if (listFile.getName().equals(path))
						;
					currentPath = currentPath + "/" + path;
					// pw.println(currentPath);
					folder = new File(currentPath);
					long size = getDirSize(listFile, 0);
					pw.println("<File>  " + listFile.getName() + "  " + size / 1024 + "KB\n\0");
					temp++;
					break;
				}
			}

		}
		if (temp == 0) {
			pw.println("unknown dir \n\0");
		}
	}

	/**
	 * 鍔熻兘锛氱紪鍐檊et鏂规硶锛屽疄鐜癠DP鏁版嵁浼犻�� 鍙傛暟锛� 杩斿洖鍊硷細 瀹炵幇锛氶�氳繃璇诲彇get鍚庣殑鏂囦欢淇℃伅锛屽垽鍒枃浠剁殑浣嶇疆锛屽疄鐜版枃浠剁殑涓嬭浇骞朵紶杈撹嚦瀹㈡埛绔�
	 * 娉ㄦ剰锛�1.瀵逛簬UDP鐨勭悊瑙�
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void get(String path) throws IOException, InterruptedException {

		String allfile = new String();
		int temp = 0;

		for (File listFile : folder.listFiles()) { // 鏂囦欢鐨勭洰褰曞惊鐜�
			if (listFile.isFile()) {
				if (listFile.getName().equals(path)) {

					pw.println("begin transfor");
					pw.println(currentPath + "/" + path + "\n\0");
					temp++;
//					 download(currentPath + "/" + path);

					break;
				}
			}
		}
		if (temp == 0) {
			pw.println("unknow file\n\0");
		}

	}

	/*
	 * 鍔熻兘锛氳幏鍙栨寚瀹氭枃浠剁殑澶у皬
	 * 
	 * @param folder:褰撳墠鏂囦欢澶�
	 * 
	 * @param size:璁板綍鏂囦欢鐨勫ぇ灏� return : 姣忎釜鏂囦欢鐨勫ぇ灏� 瀹炵幇锛氶�氳繃璇诲彇鐗瑰畾鏂囦欢鐨勫湴鍧�锛岃幏鍙栬鏂囦欢鐨勫ぇ灏忥紝骞朵笖杩斿洖缁欒皟鐢ㄧ殑鍑芥暟
	 */
	public long getDirSize(File folder, long size) {
		for (File FileList : folder.listFiles()) {
			if (FileList.isFile()) {
				size += FileList.length();
			} else if (FileList.isDirectory()) {
				size += getDirSize(FileList, size);
			}
		}
		return size;
	}

}