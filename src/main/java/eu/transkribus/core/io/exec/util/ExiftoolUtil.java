/*******************************************************************************
 * Copyright (c) 2013 DEA.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     DEA - initial API and implementation
 ******************************************************************************/
package eu.transkribus.core.io.exec.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ExiftoolUtil {	
	public static boolean isWin = System.getProperty("os.name").indexOf("win")>=0;
	
	public static final String exiftool = "exiftool";
	public static final String ERROR_TAG_NAME = "Error";
	public static final String UNKNOWN_FILE_TYPE_ERROR_MESSAGE="Unknown file type";
	public static final String MIMETYPE_TAG_NAME = "MIMEType";
	public static final String FILE_TYPE_TAG_NAME = "FileType";
	
	static String addApostrophe(String fn) {
		if (isWin)
			return "\""+fn+"\"";
		else return fn;
	}

	public static List<String> runExiftool(File file) throws IOException, TimeoutException, InterruptedException {
		return runExiftool(file, false);
	}
	
	public static List<String> runExiftool(String filepath) throws IOException, TimeoutException, InterruptedException {
		return runExiftool(filepath, false);
	}
	
	public static List<String> runExiftool(File file, boolean readNumericalTagValues) throws IOException, TimeoutException, InterruptedException {
		return runExiftool(file.getAbsolutePath(), readNumericalTagValues);
	}
	
	public static List<String> runExiftool(String filepath, boolean readNumericalTagValues) throws IOException, TimeoutException, InterruptedException {
		LinkedList<String> stdOut=new LinkedList<String>(), stdErr=new LinkedList<String>();
		String[] command;
		filepath = addApostrophe(filepath);
		if(readNumericalTagValues) {
			//-n: tag values will not be mapped to labels in the output, e.g. "Orientation = 6" instead of "Rotate 90 CW"
			command = new String[] { exiftool, "-s", "-n", filepath };
		} else {
			command = new String[] { exiftool, "-s", filepath };
		}
				
		try {
			CommandLine.runProcess(0, stdOut, stdErr, command);
		} catch (IOException | TimeoutException | InterruptedException e) {
			HashMap<String, String> tags = parseTags(stdOut);
			String error = tags.get(ERROR_TAG_NAME);
			if (error != null && error.equals(UNKNOWN_FILE_TYPE_ERROR_MESSAGE)) {
				// ignore unknown file type error!
				System.err.println("Ignoring unknown file type error: "+filepath);
			} else {
				throw e;
			}
		}
		
		return stdOut;
	}
	
	public static HashMap<String, String> parseTags(String filename, boolean readNumericalTagValues) throws IOException, TimeoutException, InterruptedException {
		
		List<String> stdOut = runExiftool(filename, readNumericalTagValues);
		return parseTags(stdOut);
	}
	
	private static HashMap<String, String> parseTags(List<String> exifStdOut) {
		HashMap<String, String> tags = new HashMap<String, String>();
		
		for (int i=0; i<exifStdOut.size(); ++i) {
			int colonIndex = exifStdOut.get(i).indexOf(":");
			if (colonIndex < 0) continue;
			if (colonIndex == exifStdOut.get(i).length()-1) continue;
			
			String tagName = exifStdOut.get(i).substring(0, colonIndex).trim();
			String tagValue = exifStdOut.get(i).substring(colonIndex+1).trim();
			tags.put(tagName, tagValue);
			
//			System.out.println("tag = "+tagName);
//			System.out.println("value = "+tagValue);
		}
		return tags;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<String> tags=null;
		try {
			tags = runExiftool("test.jpg");
			parseTags(tags);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
