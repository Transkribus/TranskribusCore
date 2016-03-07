package eu.transkribus.core.util;

import java.util.regex.Pattern;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.exceptions.InvalidUserInputException;
import eu.transkribus.core.rest.ApplicationConst;

public class UserInputChecker {
	private final static Logger logger = LoggerFactory.getLogger(UserInputChecker.class);
	
	public static String USERNAME_PATTERN = "[a-z0-9_-]{3,20}";
	public static String PASSWORD_PATTERN = ".{4,50}";
	public static String FIRST_OR_LAST_NAME_PATTERN = ".{1,50}";
	public static String EMAIL_PATTERN = ".{3,320}"; // check email with better pattern??
	public static String ORCID_PATTERN = "[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}";
	
	public static void checkEmailBlackListed(String email) throws InvalidUserInputException {
		// check email name:
		if (EMailBlacklist.isBlacklisted(email)) {
			logger.debug("Email domain is on our blacklist: "+email);
			throw new InvalidUserInputException("Email domain is on our blacklist!");
		}
	}
	
	public static void checkEmail(String email) throws InvalidUserInputException {
		// check email name:
		if (StringUtils.isEmpty(email) || !Pattern.matches(EMAIL_PATTERN, email)) {
			logger.debug("Invalid or no email specified: "+email);
			throw new InvalidUserInputException("Invalid or no email specified!");			
		}
	}
	
	public static void checkValidApplication(String application) throws InvalidUserInputException {
		// check application name:
		if (!ApplicationConst.isValidApplication(application)) {
			logger.debug("invalid application name: "+application);
			throw new InvalidUserInputException("Invalid or no application specified!"); // FIXME: shall we give the client this info???
		}
	}

	public static void checkFirstOrLastName(String name) throws InvalidUserInputException {
		if (StringUtils.isEmpty(name) || !Pattern.matches(FIRST_OR_LAST_NAME_PATTERN, name)) {
			logger.debug("first or last name too large: "+name);
			throw new InvalidUserInputException("First- or last name is too large: at most 50 characters can be specified!");			
		}
	}
	
	public static void checkUsername(String username) throws InvalidUserInputException {
		if (!Pattern.matches(USERNAME_PATTERN, username)) {
			logger.debug("username does not match pattern: "+username);
			throw new InvalidUserInputException("Username does not match the pattern: only small latin letters, underscores (_) and hyphens (-) are allowed!");			
		}
	}
	
	public static void checkOrcid(String orcid) throws InvalidUserInputException {
		if(orcid != null && !orcid.isEmpty() && !Pattern.matches(ORCID_PATTERN, orcid)){
			logger.debug("ORCID does not match pattern: "+orcid);
			throw new InvalidUserInputException("ORCID is not valid!");			
		}
	}
	
	public static void checkPassword(String pw) throws InvalidUserInputException {
		if (StringUtils.isEmpty(pw) || !Pattern.matches(PASSWORD_PATTERN, pw)) {
			String errorMsg = "Password does not match the pattern: at least 4 and at most 20 characters have to be specified!";
			logger.debug("password does not match pattern: "+pw);
			throw new InvalidUserInputException(errorMsg);
		}
	}
	
	public static void checkCollectionName(String collName) throws InvalidUserInputException {
		if (StringUtils.isEmpty(collName)) {
			String msg = "Collection name is empty!";
			throw new InvalidUserInputException(msg);
		}
		
	}
	
	public static void main(String[] args) {
//		String email = "whatever@wegwerfemail.de";
		String email = "whatever@trbvm.com";
		try {
			checkEmailBlackListed(email);
			logger.info("mail is ok: "+email);
		} catch (InvalidUserInputException e) {
			e.printStackTrace();
		}
		
		
		
	}
}
