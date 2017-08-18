package eu.transkribus.core.io.util;

import java.io.File;
import java.io.FileFilter;

import org.dea.fimgstoreclient.utils.MimeTypes;

import eu.transkribus.core.io.LocalDocConst;

public class MdFileFilter implements FileFilter {

	final static public String XML_MIME = MimeTypes.MIME_APPLICATION_XML;

	@Override
	public boolean accept(File file) {
		final String fileName = file.getName();
		return !file.isDirectory() && fileName.equals(LocalDocConst.METADATA_FILENAME);
	}
}
