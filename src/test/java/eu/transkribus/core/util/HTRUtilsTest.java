package eu.transkribus.core.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.util.HtrUtils;

public class HTRUtilsTest {
	private final static Logger logger = LoggerFactory.getLogger(HTRUtilsTest.class);

	public HTRUtilsTest() {
	}
	
	
	public static String readTestFile(String filename) {
		String str="";
		try (
			FileInputStream fin = new FileInputStream(filename);
		    GZIPInputStream gzis = new GZIPInputStream(fin);
		    InputStreamReader xover = new InputStreamReader(gzis);
		    BufferedReader is = new BufferedReader(xover);
				) {
		    String line;
		    while ((line = is.readLine()) != null) {
		    	str += (line + "\n");
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	public static void main(String[] args) {
		String str = readTestFile("/home/sebastianc/lattice_tool/YVXZBLDZLTLRHJDBPQWMTZTX_1_8_r1_r210.gz");
		HtrUtils.getnBestMatrix(str, false);

		
//		String filename = "/home/sebastianc/lattice_tool/YVXZBLDZLTLRHJDBPQWMTZTX_1_8_r1_r210.gz";
//		InputStream fileStream = new FileInputStream(filename);
//		InputStream gzipStream = new GZIPInputStream(fileStream);
//		Reader decoder = new InputStreamReader(gzipStream, encoding);
//		BufferedReader buffered = new BufferedReader(decoder);
		
		
	}

}
