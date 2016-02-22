package eu.transkribus.core.io.exec.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLine {

	private final static Logger logger = LoggerFactory.getLogger(CommandLine.class);

	public static HashSet<Process> processes = new HashSet<Process>();

	public static Integer runProcess(long timeoutMs, String... command) throws IOException, TimeoutException, InterruptedException {
		LinkedList<String> stdOut = new LinkedList<String>();
		LinkedList<String> stdErr = new LinkedList<String>();
		return runProcess(timeoutMs, stdOut, stdErr, command);
	}
	
	public static Integer runProcess(long timeoutMs, File workingDirectory, String... command) throws IOException, TimeoutException, InterruptedException {
		LinkedList<String> stdOut = new LinkedList<String>();
		LinkedList<String> stdErr = new LinkedList<String>();
		return runProcess(timeoutMs, stdOut, stdErr, workingDirectory, new HashMap<String, String>(), command);
	}

	public static Integer runProcess(long timeoutMs, File workingDirectory, Map<String, String> env, String... command) throws IOException, TimeoutException, InterruptedException {
		LinkedList<String> stdOut = new LinkedList<String>();
		LinkedList<String> stdErr = new LinkedList<String>();
		return runProcess(timeoutMs, stdOut, stdErr, workingDirectory, env, command);
	}
	
	public static Integer runProcess(long timeoutMs, Map<String, String> env, String... command) throws IOException, TimeoutException, InterruptedException {
		LinkedList<String> stdOut = new LinkedList<String>();
		LinkedList<String> stdErr = new LinkedList<String>();
		return runProcess(timeoutMs, stdOut, stdErr, null, env, command);
	}

	public static Integer runProcess(long timeoutMs, LinkedList<String> stdOut,
			LinkedList<String> stdErr, String... command) throws IOException, TimeoutException, InterruptedException {
		return runProcess(timeoutMs, stdOut, stdErr, null, new HashMap<String, String>(), command);
	}

	/**
	 * Runs a process with the given command and its parameters. Note that all
	 * whitespaces in the command list are escaped!
	 * 
	 * @param timeoutMs
	 *            A timeout in milliseconds after which the process will be
	 *            terminated and an Exception will be thrown. Set to 0 for no
	 *            timeout!
	 * @param stdOut
	 *            A list of strings with the standard output
	 * @param stdErr
	 *            A list of strings with the standard error output
	 * @param command
	 *            The command and its options. IMPORTANT NOTE: all whitespaces
	 *            in each given option string are escaped (for filenames with
	 *            spaces!). That means you have to specify an option flag and
	 *            its value parameter as different strings, e.g.: 'gm convert
	 *            -scale 120x120' should be given as an array of ['gm',
	 *            'convert', '-scale', '120x120']
	 * @throws IOException 
	 * @throws TimeoutException 
	 * @throws InterruptedException 
	 * @throws Exception
	 *             If something goes wrong, i.e. the exit code of the
	 *             application is greater than 0
	 */
	public static Integer runProcess(long timeoutMs, final LinkedList<String> stdOut,
			final LinkedList<String> stdErr, File workingDirectory, Map<String, String> env, String... command) throws IOException, TimeoutException, InterruptedException {
		stdOut.clear();
		stdErr.clear();

		ProcessBuilder pb = new ProcessBuilder();
		pb.command(command);
		pb.environment().putAll(env);

		String totalCmd = "";
		for (String c : pb.command())
			totalCmd += c + " ";
		totalCmd = totalCmd.trim();
		logger.info("CommandLine, running command = " + totalCmd);

//		pb.toString();
		if(workingDirectory != null){
			pb.directory(workingDirectory);
		}
		final Process pr = pb.start();
		
		// Handle stdout...
		final Thread outReader = new Thread() {
		    public void run() {
		    try {
		    		listOutput(stdOut, pr.getInputStream());
		        } catch (Exception e) {
		        	logger.error("Could not read stdOut.", e);
		        }
		    return;
		    }
		};

		// Handle stderr...
		final Thread errReader = new Thread() {
		    public void run() {
		    try {
		    		listOutput(stdErr, pr.getErrorStream());
		        } catch (Exception e) {
		        	logger.error("Could not read stdErr.", e);
		        }
		    return;
		    }
		};
		outReader.start();
		errReader.start();
		
		processes.add(pr);

		// wait for result and throw exception if exitcode > 0:
		//			int exitCode = pr.waitFor(); // old version
		// new version: create worker thread and join with timeout:
		Worker worker = new Worker(pr);
		worker.start();
		try {
//			logger.debug("Worker join() with timeout=" + timeoutMs);
			worker.join(timeoutMs);

			if (worker.exitCode == null) {
				String message = "Command: >" + totalCmd + "< stopped stopped due to timeout of "
						+ timeoutMs + "ms\n";
				throw new TimeoutException(message);
			}
			
			// read stdin/stdout only if no timeout occurs. otherwise this blocks the process interrupt!
			// Note: this did no work. If lots of console output is written, the buffer runs full and process hangs.
//			listOutput(stdOut, pr.getInputStream());
//			listOutput(stdErr, pr.getErrorStream());
		} catch (InterruptedException ex) {
			worker.interrupt();
			Thread.currentThread().interrupt();
			throw ex;
		} finally {
			worker.interrupt();
			//wait for std readers to finish
			errReader.join();
			outReader.join();
			processes.remove(pr);
		}

		// catch exitcode:
		logger.info("exit code = " + worker.exitCode);
		if (worker.exitCode != 0) {// || logger.isDebugEnabled()) {
			String stdoutstr = "", stderrstr = "";
			
			for(Iterator<String> it = stdOut.iterator(); it.hasNext(); ){
				stdoutstr += it.next() + "\n";
			}
			for(Iterator<String> it = stdOut.iterator(); it.hasNext(); ){
				stderrstr += it.next() + "\n";
			}
			String message = "Command: >" + totalCmd + "< stopped with exit code "
					+ worker.exitCode + "\n";
			if (!stdoutstr.isEmpty())
				message += "standard output: " + stdoutstr + "\n";
			if (!stderrstr.isEmpty())
				message += "standard error: " + stderrstr + "\n";
			logger.error(message);
		}
		
		return worker.exitCode;
	}

	private static void listOutput(LinkedList<String> stdOut, InputStream is) throws IOException {
		BufferedReader stdReader = new BufferedReader(new InputStreamReader(is));
		String out;
		while ((out = stdReader.readLine()) != null ) {
			if(!out.trim().isEmpty())
				stdOut.add(out);
		}
		stdReader.close();
	}

	private static class Worker extends Thread {
		private final Process process;
		private Integer exitCode = null;

		private Worker(Process process) {
			this.process = process;
		}

		public void run() {
			try {
				exitCode = process.waitFor();
			} catch (InterruptedException ignore) {
				process.destroy();
//				logger.error(ignore.getMessage(), ignore);
				return;
			}
		}
	}

	public static void main(String[] args) {
		String filename = "/home/sebastianc/Bilder/heidi mit leerzeichen   (1  ).jpg".replaceAll(
				" ", "\\ ");
		logger.info("File: " + (new File(filename).exists()));

		//			filename = filename.replaceAll(' ', "\\ ");
		//			filename = filename.replaceAll(" ", "%20");

		//			String sed = "$fn = $( echo "+filename+" | sed 's/ /\\ /g' )";
		//			try {
		////				runProcess("exiftool -s "+filename, 0);
		//				runProcess(sed+" | exiftool -s $fn", 0);
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//			

		ProcessBuilder pb = new ProcessBuilder();
		//			pb.command(command);
		pb.command("exiftool", "-s", filename);
		pb.redirectErrorStream(true);

		try {

			Process p = pb.start();
			BufferedReader stdOutReader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String out;
			while ((out = stdOutReader.readLine()) != null) {
				logger.info(out);
			}

			BufferedReader stdErrorReader = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			String err;
			while ((err = stdErrorReader.readLine()) != null) {
				logger.error(err);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		
	}
}
