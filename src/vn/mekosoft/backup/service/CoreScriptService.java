package vn.mekosoft.backup.service;

import java.io.IOException;

public interface CoreScriptService {
    void execute(String command) throws IOException, InterruptedException;
}
