package eu.transkribus.core.io.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import eu.transkribus.core.io.formats.XmlFormat;
import eu.transkribus.core.util.XmlUtils;

public class XmlnsFileFilter implements FileFilter {
	private final XmlFormat format;
	private final String name;
	
	public XmlnsFileFilter(final String nameWoExtension, XmlFormat format){
		this.format = format;
		this.name = nameWoExtension;
	}
	
	@Override
	public boolean accept(File pathname) {	
		if(!pathname.getName().endsWith(".xml")){
			return false;
		}
		
		if(!FilenameUtils.getBaseName(pathname.getName()).equals(name)){
			return false;
		}
		
		try {
			//open file, get namespace and match to known types
			XmlFormat currFormat = XmlUtils.getXmlFormat(pathname);
			return this.format.equals(currFormat);			
		} catch (IOException e) {
			return false;
		}
	}
}
