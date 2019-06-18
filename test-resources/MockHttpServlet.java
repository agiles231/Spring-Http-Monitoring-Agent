package mock;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockHttpServlet extends HttpServlet {
    public MockHttpServlet() {

    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        // do nothing
    }
}
