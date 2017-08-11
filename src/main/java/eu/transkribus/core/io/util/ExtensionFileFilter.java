package eu.transkribus.core.io.util;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * A FileFilter implementation that allows to specify a specific extension that filenames have to have to be accepted.
 * Filtering can be configured to be case(in)sensitive regarding the extension and to exclude names starting with ".".
 * @author philip
 *
 */
public class ExtensionFileFilter implements FileFilter {
	private final boolean excludeStartingDot;
	private final boolean caseSensitive;
	private final String extension;
	public ExtensionFileFilter(String extension, final boolean excludeFilesStartingWithDot, final boolean caseSensitive) {
		if(StringUtils.isEmpty(extension)) {
			throw new IllegalArgumentException("No extension was given.");
		}
		this.excludeStartingDot = excludeFilesStartingWithDot;
		this.caseSensitive = caseSensitive;
		//remove starting dot from extension, if any
		if(extension.startsWith(".")) {
			extension = extension.substring(1);
		}
		//no other dots allowed
		if(extension.contains(".")){
			throw new IllegalArgumentException("Extension may not include a dot.");
		}
		this.extension = extension;
		
	}

	@Override
	public boolean accept(File pathname) {
		if(!pathname.isFile() || (excludeStartingDot && pathname.getName().startsWith("."))){
			return false;
		}
		if(caseSensitive) {
			return FilenameUtils.isExtension(pathname.getName(), extension);
		} else {
			final String ext = FilenameUtils.getExtension(pathname.getName());
			return extension.equalsIgnoreCase(ext);
		}
	}	

}
