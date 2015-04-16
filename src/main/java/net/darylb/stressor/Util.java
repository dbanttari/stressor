package net.darylb.stressor;

import java.io.File;
import java.io.FileOutputStream;

public class Util {

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
	
}
