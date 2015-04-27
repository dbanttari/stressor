package net.darylb.stressor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
	
	private static Logger log = LoggerFactory.getLogger(Util.class);

	static void writeFile(File path, String name, String content) {
		File f = new File(path, name);
		FileOutputStream out;
		try {
			out = new FileOutputStream(f);
			out.write(content.getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getTimestamp() {
		Calendar c = Calendar.getInstance();
		StringBuffer timestamp = new StringBuffer();
		timestamp
			.append(c.get(Calendar.YEAR))
			.append(nn(c.get(Calendar.MONTH)))
			.append(nn(c.get(Calendar.DATE)))
			.append(nn(c.get(Calendar.HOUR)))
			.append(nn(c.get(Calendar.MINUTE)))
			.append(nn(c.get(Calendar.SECOND)));
		return timestamp.toString();
	}
	private static String nn(int n) {
		if(n < 10) {
			return "0" + Integer.toString(n);
		}
		return Integer.toString(n);
	}
	
	static void loadProperties(Properties props) {
		File f = new File("stressor.properties");
		if(f.exists()) {
			InputStream in;
			try {
				in = new FileInputStream(f);
				InputStreamReader reader = new InputStreamReader(in);
				props.load(reader);
				in.close();
			}
			catch (IOException e) {
				log.error("Problem readin stressor.properties", e);
			}
		}
		else {
			log.info("stressor.properties not found in working directory.");
		}
	}
	
}
