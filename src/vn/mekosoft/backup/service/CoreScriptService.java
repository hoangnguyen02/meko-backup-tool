package vn.mekosoft.backup.service;

import java.io.IOException;

public interface CoreScriptService {
    void executeAll(String command) throws IOException, InterruptedException;
}
