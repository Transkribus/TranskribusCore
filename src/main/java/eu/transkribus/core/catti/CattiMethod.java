package eu.transkribus.core.catti;

public enum CattiMethod {
	SET_PREFIX,
	REJECT_SUFFIX;
	
	public static CattiMethod fromStr(String str) {
		if (str == null)
			return null;
		str = str.toLowerCase();
		
		if (str.equals("setprefix") || str.equals("set_prefix"))
			return SET_PREFIX;
		else if (str.equals("rejectsuffix") || str.equals("reject_suffix"))
			return REJECT_SUFFIX;
		
		return null;
	}
}
