package eu.transkribus.core.model.beans.enums;

public enum Task {
	layoutAnalysis,
	utility,
	recognition;
	
//	layoutAnalysis("la"),
//	utility("util"),
//	recognition("recog");
//	private String taskStr;
//
//	private Task(String t){
//		taskStr = t;
//	}
//	
//	public String getString(){
//		return taskStr;
//	}
//	
//	public static Task resolve(String str){
//		for(Task t : Task.values()){
//			if(t.getString().equals(str)) {
//				return t;
//			}
//		}
//		return null;
//	}
}
