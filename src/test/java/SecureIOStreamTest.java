import org.junit.Before;
import org.junit.Test;
import student.examples.IOStream;
import student.examples.SecureIOStream;

import java.io.*;

import static org.junit.Assert.*;

public class SecureIOStreamTest {

    File file = new File("src/main/resources/test.data");

    @Test
    public void sendAndReceiveTest() throws Exception {
        int originalValue = 1;

        IOStream secureIoStream = new SecureIOStream(new BufferedOutputStream(new FileOutputStream(file)),
                new BufferedInputStream(new FileInputStream(file)));
        secureIoStream.send(1);
        int receivedValue = secureIoStream.receive();

        assertEquals(originalValue, receivedValue);
    }
}
