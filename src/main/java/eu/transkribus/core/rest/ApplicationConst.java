package eu.transkribus.core.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationConst {

	//Constants that are used in the DB to identify default collections
	public static final String ALL_APPS = "ALL";
	public static final String TRP_APP = "TRP";
	public static final String TSX_APP = "TSX";
	public static final String WEB_UI_APP = "WEB_UI";
	
//	public static final String TRP_HOMEPAGE = "https://dbis-faxe.uibk.ac.at/Transkribus";
	public static final String TRP_HOMEPAGE = "https://transkribus.eu";
	public static final String TSX_HOMEPAGE = "http://blogs.ucl.ac.uk/transcribe-bentham/";
	
	@Deprecated
	public static final String DEFAULT_COLLECTION = "TranskribusCloud";
	
	public static List<String> VALID_APPLICATIONS = new ArrayList<>();
	public static Map<String, String> APPLICATION_HP_MAP = new HashMap<>();
	static {
		VALID_APPLICATIONS.add(TRP_APP);
		VALID_APPLICATIONS.add(TSX_APP);		
		
		APPLICATION_HP_MAP.put(TRP_APP, TRP_HOMEPAGE);
		APPLICATION_HP_MAP.put(TSX_APP, TSX_HOMEPAGE); // TODO: fill in homepage for redirecting!
	}

	public static boolean isValidApplication(String app) {
		return app!=null && VALID_APPLICATIONS.contains(app.toUpperCase());
	}

}
