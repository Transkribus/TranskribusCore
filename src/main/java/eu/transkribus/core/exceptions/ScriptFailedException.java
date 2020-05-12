package eu.transkribus.core.exceptions;

public class ScriptFailedException extends Exception {
	private static final long serialVersionUID = 5568777620536872957L;
	
	private int exitCode=0;
	private String output=null;

	public ScriptFailedException() {}

	public ScriptFailedException(String message, int exitCode, String output) {
		super(message);
		this.exitCode = exitCode;
		this.output = output;
	}

	public int getExitCode() {
		return exitCode;
	}

	public void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	@Override
	public String toString() {
		return "ScriptFailedException [message="+getMessage()+", exitCode=" + exitCode + ", output=" + output + "]";
	}

}
