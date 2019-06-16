package lab3;

import java.math.BigInteger;

public class RSA_privateKey {
	private   BigInteger n;
	private   BigInteger e;
	public BigInteger getN() {
		return n;
	}
	public void setN(BigInteger n) {
		this.n = n;
	}
	public BigInteger getE() {
		return e;
	}
	public void setE(BigInteger e) {
		this.e = e;
	}
	public RSA_privateKey(BigInteger n, BigInteger e) {
//		super();
		this.n = n;
		this.e = e;
	}
	@Override
	public String toString() {
		return  n + " "  +e ;
	}
	
	
	
}
