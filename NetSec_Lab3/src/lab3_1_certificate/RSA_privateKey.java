package lab3_1_certificate;

import java.math.BigInteger;

public class RSA_privateKey {
	private  BigInteger n;
	private  BigInteger d;
	public BigInteger getN() {
		return n;
	}
	public void setN(BigInteger n) {
		this.n = n;
	}
	public BigInteger getD() {
		return d;
	}
	public void setD(BigInteger d) {
		this.d = d;
	}
	public RSA_privateKey(BigInteger n, BigInteger d) {
//		super();
		this.n = n;
		this.d = d;
	}
	@Override
	public String toString() {
		return  n + " " + d ;
	}
	
}
