package mock;

import com.agiles231.tomcat.http.agent.interfaces.AgentIdContainer;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

public class MockServletOutputStreamAgentIdContainer extends ServletOutputStream implements AgentIdContainer {
    Long id;
    public MockServletOutputStreamAgentIdContainer() {
    }

    @Override
    public void write(int b) throws IOException {

    }

    @Override
    public Long getAgentId() {
        return id;
    }

    @Override
    public void setAgentId(Long id) {
        this.id = id;
    }
}
