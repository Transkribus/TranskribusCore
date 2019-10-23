package eu.transkribus.core.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public class UnixWinDriveMapper {
	private List<Pair<String, String>> entries = new ArrayList<>();
	
	public UnixWinDriveMapper() {
	}
	
	public Pair<String, String> addMountPoint(String unixMountPoint, String windowsMountPoint) {
		String unixReg = CoreUtils.regularizedPath(unixMountPoint);
		String winReg = CoreUtils.regularizedPath(windowsMountPoint);
		Pair<String, String> entry = Pair.of(unixReg, winReg);
		Pair<String, String> oldEntry = null;
		
		int i = entries.indexOf(entry);
		
		if (i>=0) {
			oldEntry = entries.remove(i);
		}
		entries.add(entry);
		
		return oldEntry;
	}
	
	public String getWinMountPoint(String unixMountPoint) {
		String unixReg = CoreUtils.regularizedPath(unixMountPoint);
		return entries.stream().map(e -> e.getLeft()).filter(e -> e.equals(unixReg)).findFirst().orElse(null);
	}
	
	public String getUnixMountPoint(String winMountPoint) {
		String winReg = CoreUtils.regularizedPath(winMountPoint);
		return entries.stream().map(e -> e.getRight()).filter(e -> e.equals(winReg)).findFirst().orElse(null);
	}
	
	public List<Pair<String,String>> getMountPoints() {
		return entries;
	}
	
	public String unixToWinPath(String unixPath) {
		String regUnixPath = CoreUtils.regularizedPath(unixPath);
		
		for (Pair<String, String> e : entries) {
			if (regUnixPath.startsWith(e.getLeft())) {
				return e.getRight()+regUnixPath.substring(e.getLeft().length()); 
			}
		}
		
		return regUnixPath;
	}
	
	public String winToUnixPath(String winPath) {
		String regWinPath = CoreUtils.regularizedPath(winPath);
		
		for (Pair<String, String> e : entries) {
			if (regWinPath.startsWith(e.getRight())) {
				return e.getLeft()+regWinPath.substring(e.getRight().length()); 
			}
		}
		
		return regWinPath;
	}
	
	public String getSystemSpecificPath(String path) {
		if (SysUtils.isWin()) {
			return unixToWinPath(path);
		}
		else if (SysUtils.isLinux()) {
			return winToUnixPath(path);
		}
		else {
			return path;
		}
	}
	
	public static void main(String[] args) {
		UnixWinDriveMapper dm = new UnixWinDriveMapper();
		dm.addMountPoint("/mnt/dea_scratch/", "X:\\");
		dm.addMountPoint("/mnt/iza_retro/", "Y:\\");
		
		System.out.println(dm.getSystemSpecificPath("/mnt/iza_retro\\TRP/p2pala/models/grundbuchblaetter"));
		System.out.println(dm.winToUnixPath("X:\\hello/world\\"));
		System.out.println(dm.getMountPoints());
		
	}

}
