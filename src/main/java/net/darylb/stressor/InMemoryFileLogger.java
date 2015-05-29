package net.darylb.stressor;

import java.util.LinkedHashMap;

public class InMemoryFileLogger implements FileLogger {

	LinkedHashMap<String, String>files = new LinkedHashMap<String, String>();

	@Override
	public void logFile(String fileName, String content) {
		files.put(fileName, content);
	}

	public LinkedHashMap<String, String> getFiles() {
		return files;
	}
	
}
