package eu.transkribus.core.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.exceptions.ScriptFailedException;

public class ProcessBuilderUtil {
	private static final Logger logger = LoggerFactory.getLogger(ProcessBuilderUtil.class);
	
	public static final ShutdownHookProcessDestroyer processDestroyer = new ShutdownHookProcessDestroyer();
	
	public static ProcessBuilder createProcessBuilder(String[] cmd, boolean redirectErrorStream, String baseDir) throws IOException {
		return createProcessBuilder(Arrays.asList(cmd), redirectErrorStream, baseDir);
	}	
	
	public static ProcessBuilder createProcessBuilder(List<String> cmd, boolean redirectErrorStream, String baseDir) throws IOException {
		ProcessBuilder pb = new ProcessBuilder(cmd);
		setProcessBuilderBaseDirectory(pb, baseDir);	
		pb.redirectErrorStream(redirectErrorStream);
		return pb;
	}
	
	public static void setProcessBuilderBaseDirectory(ProcessBuilder pb, String scriptDir) {
		if (!StringUtils.isEmpty(scriptDir)) {
			logger.trace("Using given script dir as process base directory: "+scriptDir);
			pb.directory(new File(scriptDir));
		}
	}	
	
	public static Pair<Process, StreamGobbler> startProcessWithStreamGobbler(ProcessBuilder pb, String name, FileWriter fileWriter) throws IOException {
		pb.redirectErrorStream(true);
		Process p = pb.start();
		Long pid = SysUtils.processId(p);
		StreamGobbler sg = new StreamGobbler(p.getInputStream());
		sg.setName(name+"_"+pid);
		sg.start();	
		if (fileWriter!=null) {
			sg.setFileWriter(fileWriter);
		}
		
		return Pair.of(p, sg);
	}
	
	public static void startAndWaitForProcess(ProcessBuilder pb, String name, String desc, FileWriter fileWriter, boolean addOutputToException, boolean addShutdownHook) throws IOException, InterruptedException, ScriptFailedException {
		Pair<Process, StreamGobbler> res = startProcessWithStreamGobbler(pb, desc, fileWriter);
		if (addShutdownHook) {
			processDestroyer.add(res.getLeft());
		}
		try {
			int exitCode = res.getLeft().waitFor();
			if (exitCode != 0) {
				String msg = "'"+desc+"' failed, exitCode = "+exitCode;
				logger.error(msg);
				throw new ScriptFailedException(msg, exitCode, addOutputToException ? res.getRight().getText() : "");
			}			
		} finally {
			if (addShutdownHook) {
				processDestroyer.remove(res.getLeft());
			}			
		}
	}
}
