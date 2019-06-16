package lab3_1_certificate;

public class Certificate {
	private  String publicKey;
	private  String AlgForm;
	private   String hashForm;
	private  String time;
	public Certificate(String publicKey, String algForm, String hashForm, String time) {
//		super();
		this.publicKey = publicKey;
		AlgForm = algForm;
		this.hashForm = hashForm;
		this.time = time;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getAlgForm() {
		return AlgForm;
	}
	public void setAlgForm(String algForm) {
		AlgForm = algForm;
	}
	public String getHashForm() {
		return hashForm;
	}
	public void setHashForm(String hashForm) {
		this.hashForm = hashForm;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	@Override
	public String toString() {
		return "Certificate [publicKey=" + publicKey + ", AlgForm=" + AlgForm + ", hashForm=" + hashForm + ", time="
				+ time + "]";
	}
	
}
