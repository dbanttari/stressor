package net.darylb.stressor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.http.HttpEntity;

public abstract class AbstractHttpAction extends Action {

	byte[] readContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
		return readContentFromEntity(entity, 16384);
	}
	byte[] readContentFromEntity(HttpEntity entity, int maxLen) throws IllegalStateException, IOException {
	    byte[] buf = new byte[maxLen];
	    int pos = 0;
	    InputStream instream = entity.getContent();
	    try {
			int bytes;
	        while( (bytes=instream.read(buf, pos, buf.length-pos)) > 1) {
	        	pos += bytes;
	        }
	    } finally {
	        instream.close();
	    }
	    return Arrays.copyOfRange(buf, 0, pos);
	}

}
