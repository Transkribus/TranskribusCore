package eu.transkribus.core.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import eu.transkribus.core.exceptions.WrappedException;

public class StreamUtils {

//	/**
//	 * Reads an input stream and copies its content to a byte[]
//	 * 
//	 * @param is InputStream
//	 * @return data as byte[]
//	 * @throws IOException
//	 */
//	public static ByteArrayOutputStream writeStreamToByteArr(InputStream is) throws IOException {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		BufferedInputStream bis = new BufferedInputStream(is);
//
//		int inByte;
//		while ((inByte = bis.read()) != -1) {
//			baos.write(inByte);
//		}
//		bis.close();
//		return baos;
//	}
	
	@FunctionalInterface
	public interface CheckedFunction<T,R> {
	    R apply(T t) throws Exception;
	}

	public static <T, R> Function<T, R> wrap(CheckedFunction<T, R> checkedFunction) throws WrappedException {
		return t -> {
			try {
				return checkedFunction.apply(t);
			} catch (Exception e) {
				throw new WrappedException(e);
			}
		};
	}
}
