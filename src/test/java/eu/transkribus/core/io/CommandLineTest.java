package eu.transkribus.core.io;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

import eu.transkribus.core.io.exec.util.CommandLine;

public class CommandLineTest {
	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException{
		while(true){
			LinkedList<String> stdOut = new LinkedList<>();
			LinkedList<String> stdErr = new LinkedList<>();
			CommandLine.runProcess(0, stdOut, stdErr, "cat", "/media/daten/Dokumente/TRP Technology.tex");
			Thread.sleep(1);
			for(String s : stdOut){
				System.out.println(s);
			}
			for(String s : stdErr){
				System.out.println(s);
			}
			
		}
	}
}
