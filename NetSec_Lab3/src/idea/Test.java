package idea;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String str="abcd";
		byte[] by=str.getBytes();
		String st=new String(by);
		byte[] b=st.getBytes();
		String ss=new String(b);
		System.out.println(st+" "+str+" "+ss);//abcd abcd
	}

}
