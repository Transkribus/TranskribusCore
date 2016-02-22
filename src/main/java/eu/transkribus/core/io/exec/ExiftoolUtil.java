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
package eu.transkribus.core.io.exec;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.exec.util.CommandLine;

public class ExiftoolUtil {
	private static final Logger logger = LoggerFactory.getLogger(ExiftoolUtil.class);
	public static boolean isWin = System.getProperty("os.name").indexOf("win") >= 0;

	public static final String WIDTH_KEY = "ImageWidth";
	public static final String HEIGHT_KEY = "ImageHeight";

	public static final String EXIFTOOL_COMMAND = "exiftool";

	public static List<String> runExiftool(final String filename) throws IOException, TimeoutException, InterruptedException {
//		LinkedList<String> stdOut = new LinkedList<String>();
//		LinkedList<String> stdErr = new LinkedList<String>();
//		final String[] command = { EXIFTOOL_COMMAND, "-s", CommandLine.escape(filename) };
//		CommandLine.runProcess(command, 0, stdOut, stdErr);
//		return stdOut;

//		CollectingLogOutputStream stdOut = new CollectingLogOutputStream();
//		CollectingLogOutputStream stdErr = new CollectingLogOutputStream();
		LinkedList<String> stdOut = new LinkedList<String>();
		LinkedList<String> stdErr = new LinkedList<String>();
//		Command.run(EXIFTOOL_COMMAND, 1000, stdOut, stdErr, "-s", filename);
		CommandLine.runProcess(1000, stdOut, stdErr, EXIFTOOL_COMMAND, "-s", filename);
		if(!stdErr.isEmpty()){
			for(String s : stdErr){
				logger.error(s);
			}
		}
		return stdOut;
	}

	public static HashMap<String, String> extractImgMd(final String filename) throws IOException, TimeoutException, InterruptedException {

		List<String> stdOut = runExiftool(filename);
		return parseTags(stdOut);
	}

	public static HashMap<String, String> parseTags(List<String> exifStdOut) {
		HashMap<String, String> tags = new HashMap<String, String>();

		for (int i = 0; i < exifStdOut.size(); ++i) {
			int colonIndex = exifStdOut.get(i).indexOf(":");
			if (colonIndex < 0)
				continue;
			if (colonIndex == exifStdOut.get(i).length() - 1)
				continue;

			String tagName = exifStdOut.get(i).substring(0, colonIndex).trim();
			String tagValue = exifStdOut.get(i).substring(colonIndex + 1).trim();
			tags.put(tagName, tagValue);
		}
		return tags;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//FIXME exiftool doesn't work for files with spaces in name (linux, windows untested)
		String[] files = {"/mnt/dea_scratch/TRP/test/Schauplatz_test/Frisch_ Johan_Schauplatz 1666_Page_003.jpg",
							"/home/philip/programme/NCSR_tools_testing/test/002_080_001.jpg",
							"/mnt/dea_scratch/TRP/test/I_ZvS_1902_4Q/ZS-I-1902-198_1.jpg"
							};
		List<String> tags = null;
		for(String f : files){
			try {
				
				tags = runExiftool(f);
				Map<String, String> map = parseTags(tags);
	
				logger.debug(map.get(ExiftoolUtil.HEIGHT_KEY));
				logger.debug(map.get(ExiftoolUtil.WIDTH_KEY));
				
	//			for (Entry<String, String> e : map.entrySet()) {
	//				logger.debug(e.getKey() + " - " + e.getValue());
	//			}
	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
