package eu.transkribus.core.util;

import java.io.File;
import java.io.IOException;

import eu.transkribus.core.util.ZipUtils;

public class ZipUtilsTest {
	public static void main(String[] args){
		final String zipPath = "/home/philip/TRP_DOC_1416396951661.zip";
		final File zipFile = new File(zipPath);
		
		try {
			ZipUtils.unzip(zipFile, "/tmp/zipTest");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
