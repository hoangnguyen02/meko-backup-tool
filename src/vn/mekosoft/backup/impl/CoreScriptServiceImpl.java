package vn.mekosoft.backup.impl;

import java.io.IOException;
import vn.mekosoft.backup.service.CoreScriptService;

public class CoreScriptServiceImpl implements CoreScriptService {
    @Override
    public void executeAll(String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        try {
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw e;
        }
    }
}
