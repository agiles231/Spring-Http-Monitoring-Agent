package mock;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

public class MockServletOutputStream extends ServletOutputStream {
    public MockServletOutputStream() {
    }

    @Override
    public void write(int b) throws IOException {

    }
}
