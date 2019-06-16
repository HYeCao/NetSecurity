package file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 此文件用于：1. MD5应用的加密实现（针对文件进行加密的操作）
 *            2. 文件的传输的实现（包括文本文件+图片文件）
 *             
 * @author dell
 *
 */
public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File file=new File("1.JPG");
//		FileOutputStream fp=new FileOutputStream(file);
	    FileInputStream fi=new FileInputStream(file);
//	    fi.read(b);
	    int ch=0;
	    fi = new FileInputStream(file);            
        int fileLen = fi.available();
        
        System.out.println(fileLen);
	    while ((ch = fi.read()) != -1) {  
//            System.out.print((char) ch);  
        }  
	    
	    /**
	     * 此处尝试：获取图片文件的字节转化为string 客户之间传输string 
	     * 将string转化为byte实现图片的文件的创建填充
	     */
//	    
//	  //获取图片字节流
////	    FileInputStream fis = new FileInputStream("client.bmp");
//	    //获取输出流
////	    OutputStream out = socket.getOutputStream();
//	    byte[] buf = new byte[1024];
//	    int len = 0;
//	    //2.往输出流里面投放数据
//	    while ((len = fis.read(buf)) != -1)
//	    {
////	    out.write(buf,0,len);
//	    }
//	    //通知服务端，数据发送完毕
////	    s.shutdownOutput();
//	    //3.获取输出流，接受服务器传送过来的消息“上传成功”
////	    InputStream in = s.getInputStream();
//	    byte[] bufIn = new byte[1024];
////	    int num = in.read(bufIn);
////	    System.out.println(new String(bufIn,0,num));
//	    //关闭资源
//	    fis.close();
////	    out.close();
////	    in.close();
////	    s.close();

	}

}
