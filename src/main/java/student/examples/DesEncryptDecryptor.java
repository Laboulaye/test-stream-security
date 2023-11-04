package student.examples;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

public class DesEncryptDecryptor {

    /**
     * Hardcoded secret string
     */
    static final String SECRET_KEY = "SecretKey";
    /**
     * Flag to encrypt/decrypt bytes in CTR mode. true = in CTR mode
     */
    private boolean isCTRMode;

    private SecretKey secretKey;
    private Cipher cipher;
    private IvParameterSpec ivSpec;
    private byte[] ivBytes = new byte[8];


    public DesEncryptDecryptor(boolean isCTRMode) {
        this.isCTRMode = isCTRMode;
    }

    public byte[] encryptMessage(byte[] intBytes) throws Exception {
        createSecretKey();
        createCipher(Cipher.ENCRYPT_MODE);
        return encryptBytes(intBytes);
    }

    public byte[] decryptMessage(byte[] encryptedBytes) throws Exception {
        createSecretKey();
        createCipher(Cipher.DECRYPT_MODE);
        return decryptBytes(encryptedBytes);
    }


                                        //Private methods
    //////////////////////////////////////////////////////////////////////////////////////////////////

    private void createSecretKey() throws Exception {
        // Create SecretKey using hardcoded string as key and DESKeySpec
        DESKeySpec desKeySpec = new DESKeySpec(SECRET_KEY.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        secretKey = keyFactory.generateSecret(desKeySpec);
    }

    private void createCipher(int decryptMode) throws Exception {
        if (!isCTRMode) {
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(decryptMode, secretKey);
        } else {
            ivSpec = new IvParameterSpec(ivBytes);
            cipher = Cipher.getInstance("DES/CTR/NoPadding");
            cipher.init(decryptMode, secretKey, ivSpec);
        }
    }

    private byte[] encryptBytes(byte[] intBytes) throws Exception {
        byte[] encryptedBytes;
        if (!isCTRMode) {
            encryptedBytes = cipher.doFinal(intBytes);
        } else {
            encryptedBytes = encryptBytesInCTRMode(intBytes);
        }

        //Convert the encrypted bytes to Base64 for easy display
        String encryptedMessage = Base64.getEncoder().encodeToString(encryptedBytes);
        System.out.println("Encrypted message sent: " + encryptedMessage);
        return encryptedBytes;
    }

    private byte[] encryptBytesInCTRMode(byte[] intBytes) throws Exception {
        byte[] encryptedBytes = new byte[intBytes.length];

        // Generating the keystream
        byte[] keyStream = new byte[intBytes.length];
        byte[] counter = ivBytes.clone(); // start with IV as a counter

        for (int i = 0; i < intBytes.length; i++) {
            // Update the cipher with a new counter for each byte
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(counter));
            byte[] counterEncrypted = cipher.doFinal(new byte[] {0}); // encrypt an empty block to get the next byte of the keystream
            keyStream[i] = counterEncrypted[0]; // get the next byte of the keystream

            // Increment the counter for generating a new block of the keystream
            incrementCounter(counter);
        }

        // Encrypt the data using XOR operation
        for (int i = 0; i < intBytes.length; i++) {
            encryptedBytes[i] = (byte) (intBytes[i] ^ keyStream[i]); // XOR
        }

        return encryptedBytes;
    }

    private byte[] decryptBytes(byte[] encryptedBytes) throws Exception {
        if (!isCTRMode) {
            return cipher.doFinal(encryptedBytes);
        } else {
            return decryptBytesInCTRMode(encryptedBytes);
        }
    }

    private byte[] decryptBytesInCTRMode(byte[] encryptedBytes) throws Exception {
        byte[] decryptedBytes = new byte[encryptedBytes.length];
        byte[] keyStream = new byte[encryptedBytes.length];
        // Re-generate the keystream using the IV
        byte[] counter = ivBytes.clone();
        for (int i = 0; i < encryptedBytes.length; i++) {
            // Re-encrypt an empty block to regenerate the keystream
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(counter));
            byte[] counterEncrypted = cipher.doFinal(new byte[] {0});
            keyStream[i] = counterEncrypted[0];

            incrementCounter(counter);
        }

        // Perform XOR operation to decrypt the data
        for (int i = 0; i < encryptedBytes.length; i++) {
            decryptedBytes[i] = (byte) (encryptedBytes[i] ^ keyStream[i]); // XOR
        }
        return decryptedBytes;
    }

    private static void incrementCounter(byte[] counter) {
        for (int i = counter.length - 1; i >= 0; i--) {
            if (++counter[i] != 0) {
                break;
            }
        }
    }

}
