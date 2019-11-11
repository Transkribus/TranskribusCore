package eu.transkribus.core.util;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;

public class StreamGobbler extends Thread {
    InputStream is;
    Logger logger;
    String text="";
    FileWriter writer;
    boolean cancelled=false;

    // reads everything from is until empty. 
    public StreamGobbler(InputStream is) {
        this.is = is;
    }
    
    public StreamGobbler(InputStream is, Logger logger) {
        this.is = is;
        this.logger = logger;
    }
    
    public void setFileWriter(FileWriter writer) {
    	this.writer = writer;
    }
    
    public String getText() {
    	return text;
    }
    
    public void cancel() {
    	this.cancelled = true;
    }
    
    public void run() {
        try (InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);) {
            String line=null;
            while ( (line = br.readLine()) != null) {
            	if (cancelled) {
            		break;
            	}
            	
            	if (logger != null) {
            		logger.info(line);
            	}
            	else {
            		System.out.println(line);
            	}
            	text += line+"\n";
            	if (writer != null) {
                	writer.write(line+"\n");
                	writer.flush();            		
            	}
            }
        } catch (IOException ioe) {
        	if (logger != null) {
        		logger.error(ioe.getMessage(), ioe);
        	}
        	else {
        		ioe.printStackTrace();
        	}
        }
    }
}