package eu.transkribus.core.util;

import java.util.regex.Pattern;

import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.exceptions.InvalidUserInputException;
import eu.transkribus.core.rest.ApplicationConst;

public class UserInputChecker {
	private final static Logger logger = LoggerFactory.getLogger(UserInputChecker.class);
	
	public static final String USERNAME_PATTERN_STR = "[a-z0-9_-]{3,20}";
	public static final String PASSWORD_PATTERN_STR = ".{4,50}";
	public static final String FIRST_OR_LAST_NAME_PATTERN_STR = ".{1,50}";
	public static final String EMAIL_PATTERN_STR = 
			"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*"
			+ "@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
	public static final String ORCID_PATTERN_STR = "[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}[0-9X]{1}";
	
//	public static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_PATTERN_STR);
//	public static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_PATTERN_STR);
//	public static final Pattern FIRST_OR_LAST_NAME_PATTERN = Pattern.compile(FIRST_OR_LAST_NAME_PATTERN_STR);
//	public static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_PATTERN_STR);
//	public static final Pattern ORCID_PATTERN = Pattern.compile(ORCID_PATTERN_STR);
	
	public static void checkEmailBlackListed(String email) throws InvalidUserInputException {
		// check email name:
		if (EMailBlacklist.isBlacklisted(email)) {
			logger.debug("Email domain is on our blacklist: "+email);
			throw new InvalidUserInputException("Email domain is on our blacklist!");
		}
	}
	
	public static void checkEmailFormat(final String email) throws InvalidUserInputException {
		// check email name:
		if (StringUtils.isEmpty(email) || !Pattern.matches(EMAIL_PATTERN_STR, email.toLowerCase())) {
			logger.debug("Invalid or no email specified: "+email);
			throw new InvalidUserInputException("Invalid or no email specified!");			
		}
	}
	
	public static void checkEmailMxRecord(String email) throws InvalidUserInputException, NamingException {
		if (StringUtils.isEmpty(email)) {
			throw new InvalidUserInputException("email is null or empty");			
		}
		final int nrOfMailServers = EMailMxLookup.doLookupAddress(email);
		if(nrOfMailServers < 1) {
			throw new InvalidUserInputException("Host of email address does not accept mail!");
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
		if (StringUtils.isEmpty(name) || !Pattern.matches(FIRST_OR_LAST_NAME_PATTERN_STR, name)) {
			logger.debug("first or last name too large: "+name);
			throw new InvalidUserInputException("First- or last name is too large: at most 50 characters can be specified!");			
		}
	}
	
	public static void checkUsername(String username) throws InvalidUserInputException {
		if (!Pattern.matches(USERNAME_PATTERN_STR, username)) {
			logger.debug("username does not match pattern: "+username);
			throw new InvalidUserInputException("Username does not match the pattern: only small latin letters, underscores (_) and hyphens (-) are allowed!");			
		}
	}
	
	public static void checkOrcid(String orcid) throws InvalidUserInputException {
		if(orcid != null && !orcid.isEmpty() && !Pattern.matches(ORCID_PATTERN_STR, orcid)){
			logger.debug("ORCID does not match pattern: "+orcid);
			throw new InvalidUserInputException("ORCID is not valid!");			
		}
	}
	
	public static void checkPassword(String pw) throws InvalidUserInputException {
		if (StringUtils.isEmpty(pw) || !Pattern.matches(PASSWORD_PATTERN_STR, pw)) {
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
