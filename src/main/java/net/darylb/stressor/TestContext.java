package net.darylb.stressor;

import java.io.File;
import java.util.Properties;

public class TestContext extends Properties {

	private static final long serialVersionUID = -1605284270262743221L;
	private final File logDir;
	private final String name;

	public TestContext(String name, String logDir) {
		this.name = name;
		this.logDir = new File(logDir);
	}

	public File getLogDir() {
		return logDir;
	}

	public String getName() {
		return name;
	}

}
