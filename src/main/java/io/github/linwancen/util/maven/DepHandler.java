package io.github.linwancen.util.maven;

import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取依赖清单
 */
class DepHandler implements InvocationOutputHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DepHandler.class);

    private boolean nextIsDep = false;
    List<String> output = new ArrayList<>();

    @Override
    public void consumeLine(String s) {
        if (s.startsWith("[INFO] Dependencies classpath:")) {
            nextIsDep = true;
        } else if (nextIsDep) {
            output.add(s);
            nextIsDep = false;
        } else if (s.startsWith("[ERROR]")) {
            LOG.error(s);
        }
    }
}
