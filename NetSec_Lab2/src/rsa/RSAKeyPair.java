package rsa;


public class RSAKeyPair {

    private final RSA_privateKey privateKey;
    
    private final RSA_publicKey publicKey;
    
    public RSAKeyPair(RSA_publicKey publicKey,RSA_privateKey privateKey){
        this.privateKey=privateKey;
        this.publicKey=publicKey;
    }

    public RSA_privateKey getPrivateKey() {
        return privateKey;
    }

    public RSA_publicKey getPublicKey() {
        return publicKey;
    }

    
}
