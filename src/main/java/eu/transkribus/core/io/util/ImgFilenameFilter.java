package eu.transkribus.core.io.util;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.io.FilenameUtils;
import org.dea.fimagestore.core.util.MimeTypes;

public class ImgFilenameFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		final String mime = MimeTypes.getMimeType(FilenameUtils.getExtension(name));
		//is allowed mimetype and not starts with ".", which may occur on mac
		return !name.startsWith(".") && ImgPriority.containsMimeType(mime);
	}

}
