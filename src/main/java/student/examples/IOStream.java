package student.examples;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

public class IOStream {

    protected BufferedInputStream in;
    protected BufferedOutputStream out;


    public IOStream(BufferedOutputStream out, BufferedInputStream in) {
        super();
        this.in = in;
        this.out = out;
    }

    public void send(int value) throws Exception {
        out.write(value);
        out.flush();
    }

    public void send(byte[] bytes) throws IOException {
        out.write(bytes);
        out.flush();
    }

    public int receive() throws Exception {
        if (in.available() > 0) {
            return in.read();
        }
        return -1;
    }

    public byte[] receiveBytes() throws IOException {
        return in.readNBytes(8);
    }

}
