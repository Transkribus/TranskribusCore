package eu.transkribus.core.catti;

import java.io.Serializable;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class CattiRequest implements Serializable {
	private static final long serialVersionUID = 8929354184059771278L;

	private final static Logger logger = LoggerFactory.getLogger(CattiRequest.class);
	
	int userid;
	int docid;
	int pid;
	String lineid;
	
	CattiMethod method;
	String prefix;
	String suffix;
	boolean last_token_is_partial;
	String corrected_translation_out;
	
	String error=null;

	public CattiRequest(int userid, int docid, int pid, String lineid, CattiMethod method, String prefix, String suffix, boolean last_token_is_partial, String corrected_translation_out) {
		super();
		this.userid = userid;
		this.docid = docid;
		this.pid = pid;

		Objects.requireNonNull(lineid);
		this.lineid = lineid;

		Objects.requireNonNull(method);
		this.method = method;
		
		this.prefix = prefix==null ? "" : prefix;
		this.suffix = suffix==null ? "" : suffix;
		
		if (this.suffix.isEmpty() || this.prefix.isEmpty()) // security measure -> can lead to sigsev in c++ code!
			this.last_token_is_partial = false;
		else
			this.last_token_is_partial = last_token_is_partial;
		
		this.corrected_translation_out = corrected_translation_out==null ? "" : corrected_translation_out;
	}
		
	public static String toJsonStr(CattiRequest r) {
		if (r == null)
			return "";
		return (new Gson()).toJson(r);
	}
	
	public static CattiRequest fromJsonStr(String str) {
		try {
			return (new Gson()).fromJson(str, CattiRequest.class);
		} catch (JsonSyntaxException e) {
			logger.warn("Could not parse CattiRequest from json string: "+str+", error message: "+e.getMessage());
			return null;
		}
	}
		
	public CattiMethod getMethod() {
		return method;
	}
	public void setMethod(CattiMethod method) {
		this.method = method;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public boolean isLast_token_is_partial() {
		return last_token_is_partial;
	}
	public void setLast_token_is_partial(boolean last_token_is_partial) {
		this.last_token_is_partial = last_token_is_partial;
	}
	public String getCorrected_translation_out() {
		return corrected_translation_out;
	}
	public void setCorrected_translation_out(String corrected_translation_out) {
		this.corrected_translation_out = corrected_translation_out;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getDocid() {
		return docid;
	}

	public void setDocid(int docid) {
		this.docid = docid;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getLineid() {
		return lineid;
	}

	public void setLineid(String lineid) {
		this.lineid = lineid;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	public boolean hasError() {
		return error != null && !error.isEmpty();
	}

	public static void main(String[] args) {
		CattiRequest cr = new CattiRequest(0, 1, 2, "whatever", CattiMethod.REJECT_SUFFIX, "asdf", "asdf", false, "asdfadff");
		System.out.println("cr = "+CattiRequest.toJsonStr(cr));
		
//		CattiRequest cr1 = (new Gson()).fromJson(cr.toJsonStr(), CattiRequest.class);
		
		CattiRequest cr1 = CattiRequest.fromJsonStr(CattiRequest.toJsonStr(cr));
		
		System.out.println("cr1 = "+cr1);
		
	}

	public String toString() {
	    final String TAB = ", ";
	    String retValue = "CattiRequest ( "+super.toString();
		retValue += TAB + "userid = " + this.userid;
		retValue += TAB + "docid = " + this.docid;
		retValue += TAB + "pid = " + this.pid;
		retValue += TAB + "lineid = " + this.lineid;
		retValue += TAB + "method = " + this.method;
		retValue += TAB + "prefix = " + this.prefix;
		retValue += TAB + "suffix = " + this.suffix;
		retValue += TAB + "last_token_is_partial = " + this.last_token_is_partial;
		retValue += TAB + "corrected_translation_out = " + this.corrected_translation_out;
		retValue += TAB + "error = " + this.error;
		retValue += " )";
	    return retValue;
	}

}
