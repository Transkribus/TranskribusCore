package eu.transkribus.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;

public class StreamGobbler extends Thread {
    InputStream is;
    Logger logger;

    // reads everything from is until empty. 
    public StreamGobbler(InputStream is) {
        this.is = is;
    }
    
    public StreamGobbler(InputStream is, Logger logger) {
        this.is = is;
        this.logger = logger;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null) {
            	if (this.logger != null)
            		logger.info(line);
            	else
            		System.out.println(line);
            }
        } catch (IOException ioe) {
        	if (logger != null)
        		ioe.printStackTrace();
        	else
        		logger.error(ioe.getMessage(), ioe);
        }
    }
}