package eu.transkribus.core.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EMailBlacklist {
	private final static Logger logger = LoggerFactory.getLogger(EMailBlacklist.class);
	
	static InputStream input = EMailBlacklist.class.getResourceAsStream("/emailBlacklist.txt");
	static List<String> blackList = new ArrayList<>();
	
	static {
		if (input != null) { 
			Scanner sc = new Scanner(input);
	
			while (sc.hasNextLine()) {
				blackList.add(sc.nextLine());
			}
		}
		
		logger.debug("N mails in blacklist: "+blackList.size());
	}
	
	public static boolean isBlacklisted(String email) {
		String emailLower = email.toLowerCase();
		for (String blem : blackList) {
			if (emailLower.endsWith(blem))
				return true;
		}
		return false;
	}
	
	

}
