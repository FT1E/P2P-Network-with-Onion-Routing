import Util.LogLevel;
import Util.Logger;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.util.Base64;

public class SymmetricKey {

    // todo - think of which encrypting/decrypting algs to use

    // variables
    private final SecretKey symmetricKey;
    private final IvParameterSpec ivSpec;
    // end variables

    // Constructors
    public SymmetricKey() throws NoSuchAlgorithmException {

        KeyGenerator key_gen = KeyGenerator.getInstance("AES");
        symmetricKey = key_gen.generateKey();

        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] random = new byte[16];
        secureRandom.nextBytes(random);
        ivSpec = new IvParameterSpec(random);
    }

    public SymmetricKey(SecretKey key, byte[] randomIvSpec){
        this.symmetricKey = key;
        ivSpec = new IvParameterSpec(randomIvSpec);
    }
    // end constructors

    // encrypting and decrypting
    public String encrypt(String body){
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Logger.log("Fix code in SymmetricKey.encrypt():" + e.getMessage(), LogLevel.WARN);
            return null;
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, symmetricKey, ivSpec);
        } catch (InvalidKeyException e) {
            Logger.log("Invalid Key exception in SymmetricKey.encrypt():" + e.getMessage(), LogLevel.WARN);
            return null;
        } catch (InvalidAlgorithmParameterException e) {
            Logger.log("Invalid algo parameter exception in SymmetricKey.encrypt():" + e.getMessage(), LogLevel.WARN);
            return null;
        }
        return doFinal(cipher, body);
    }

    public String decrypt(String body){
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Logger.log("Fix code in SymmetricKey.decrypt():" + e.getMessage(), LogLevel.WARN);
            return null;
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, symmetricKey, ivSpec);
        } catch (InvalidKeyException e) {
            Logger.log("Invalid Key exception in SymmetricKey.decrypt():" + e.getMessage(), LogLevel.WARN);
            return null;
        } catch (InvalidAlgorithmParameterException e) {
            Logger.log("Invalid algo parameter exception in SymmetricKey.decrypt():" + e.getMessage(), LogLevel.WARN);
            return null;
        }
        return doFinal(cipher, body);
    }

    // Helper method
    private String doFinal(Cipher cipher, String body){
        String res = null;
        try {
            res = Base64.getEncoder().encodeToString(cipher.doFinal(body.getBytes()));
        } catch (IllegalBlockSizeException e) {
            Logger.log("Illegal Block Size exception in SymmetricKey:" + e.getMessage(), LogLevel.WARN);
        } catch (BadPaddingException e) {
            Logger.log("Bad Padding exception in SymmetricKey:" + e.getMessage(), LogLevel.WARN);
        }
        return res;
    }

    // end encrypting and decrypting
}
