package eu.transkribus.core.io.util;

import java.io.File;
import java.io.FileFilter;

import org.dea.fimgstoreclient.utils.MimeTypes;

public class MdFileFilter implements FileFilter {

	final static public String XML_MIME = MimeTypes.MIME_APPLICATION_XML;
	final static public String MD_FILENAME = "metadata.xml";

	@Override
	public boolean accept(File file) {
		final String fileName = file.getName();
		return !file.isDirectory() && fileName.equals(MD_FILENAME);
	}
}
