package file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

/**
 * ���ļ����ڣ�1. MD5Ӧ�õļ���ʵ�֣�����ļ����м��ܵĲ�����
 *            2. �ļ��Ĵ����ʵ�֣������ı��ļ�+ͼƬ�ļ���
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
	     * �˴����ԣ���ȡͼƬ�ļ����ֽ�ת��Ϊstring �ͻ�֮�䴫��string 
	     * ��stringת��Ϊbyteʵ��ͼƬ���ļ��Ĵ������
	     */
//	    
//	  //��ȡͼƬ�ֽ���
////	    FileInputStream fis = new FileInputStream("client.bmp");
//	    //��ȡ�����
////	    OutputStream out = socket.getOutputStream();
//	    byte[] buf = new byte[1024];
//	    int len = 0;
//	    //2.�����������Ͷ������
//	    while ((len = fis.read(buf)) != -1)
//	    {
////	    out.write(buf,0,len);
//	    }
//	    //֪ͨ����ˣ����ݷ������
////	    s.shutdownOutput();
//	    //3.��ȡ����������ܷ��������͹�������Ϣ���ϴ��ɹ���
////	    InputStream in = s.getInputStream();
//	    byte[] bufIn = new byte[1024];
////	    int num = in.read(bufIn);
////	    System.out.println(new String(bufIn,0,num));
//	    //�ر���Դ
//	    fis.close();
////	    out.close();
////	    in.close();
////	    s.close();

	}

}
