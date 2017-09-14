package eu.transkribus.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FileUtils;

import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;

public class ChecksumUtils {
	
	//TODO add algorithms and parameterize methods
	public enum ChkSumAlg {
		MD5;
	}
	private final static ChkSumAlg defaultChkSumAlg = ChkSumAlg.MD5;

	public static byte[] getMd5Sum(File f) throws IOException {
		byte[] md5 = null;
		if(f.canRead()){
			InputStream is = new FileInputStream(f);
	
			md5 = getMessageDigest(is, defaultChkSumAlg);
	
			is.close();
		} else {
			throw new IOException("File " + f.getAbsolutePath() + " is not readable");
		}
		return md5;
		// This can be done during some other operation like this:
		// MessageDigest md = MessageDigest.getInstance("MD5");
		// try {
		// is = new DigestInputStream(is, md);
		// // read stream to EOF as normal...
		// } finally {
		// is.close();
		// }
		// md.digest();

	}
	
	public static String getMd5SumHex(URL url) throws IOException{
		if(!url.getProtocol().contains("file")){
			throw new IllegalArgumentException(url + " is not a local file!");
		}
		final String chkSum;
		File f = FileUtils.toFile(url);
		chkSum = ChecksumUtils.getMd5SumHex(f);
		return chkSum;
	}

	public static String getMd5SumHex(File f) throws IOException {
		byte[] bytes = getMd5Sum(f);

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xff & bytes[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	private static byte[] getMessageDigest(InputStream is, ChkSumAlg chkSumAlg)
			throws IOException {

		MessageDigest md;
		try {
			md = MessageDigest.getInstance(chkSumAlg.toString());
		} catch (NoSuchAlgorithmException e) {
			// we should never get here...
			throw new IOException("Check Message digest algorithms!!");
		}

		byte[] dataBytes = new byte[1024];
		int nread = 0;
		while ((nread = is.read(dataBytes)) != -1) {
			md.update(dataBytes, 0, nread);
		}

		return md.digest();
	}
}
