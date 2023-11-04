package student.examples;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;

public class SecureIOStream  extends IOStream{

    public SecureIOStream(BufferedOutputStream out, BufferedInputStream in) {
        super(out, in);
    }

    @Override
    public void send(int value) throws Exception{
        // Convert the integer to bytes
        byte[] bytes = ByteBuffer.allocate(4).putInt(value).array();

        byte[] encryptedBytes = new DesEncryptDecryptor(true).encryptMessage(bytes);
        super.send(encryptedBytes);
    }

    @Override
    public int receive() throws Exception {
        // Read encrypted message 8 bytes
        byte[] bytes = super.receiveBytes();

        String encryptedMessage = Base64.getEncoder().encodeToString(bytes);
        System.out.println("Encrypted message received: " + encryptedMessage);

        byte[] decryptedBytes = new DesEncryptDecryptor(true).decryptMessage(bytes);

        //Modify byte[] to int
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.put(decryptedBytes);
        buffer.rewind();
        int value = buffer.getInt();
        System.out.println("Decrypted value received: " + value);

        return value;
    }

}

