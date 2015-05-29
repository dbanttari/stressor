package net.darylb.stressor;

import java.io.File;

public class FilesystemFileLogger implements FileLogger {

	private File logDir;

	public FilesystemFileLogger(String logDir) {
		this.logDir = new File(logDir);
		this.logDir.mkdirs();
	}
	
	@Override
	public void logFile(String fileName, String content) {
		Util.writeFile(getLogDir(), fileName, content);
	}

	public File getLogDir() {
		return logDir;
	}
	
}
