package eu.transkribus.core.io.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

/**
 * A {@link ImgFilenameFilter} that allows to set a base filename that that is taken into account in the accept method.
 * This allows to filter for image files that belong to a specific PAGE XML file.
 */
public class ImgBaseFilenameFilter extends ImgFilenameFilter {
	final String baseFilename;
	
	public ImgBaseFilenameFilter(final String baseFilename) {
		super();
		this.baseFilename = baseFilename;
	}
	
	@Override
	public boolean accept(File dir, String name) {
		return super.accept(dir, name) && baseFilename.equals(FilenameUtils.getBaseName(name));
	}
}
