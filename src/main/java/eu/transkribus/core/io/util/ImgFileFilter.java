package eu.transkribus.core.io.util;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.FilenameUtils;
import org.dea.fimagestore.core.util.MimeTypes;

public class ImgFileFilter implements FileFilter {
	public ImgFileFilter(){}

	@Override
	public boolean accept(File pathname) {
		final String mime = MimeTypes.getMimeType(FilenameUtils.getExtension(pathname.getName()));
		//is allowed mimetype and not starts with ".", which may occur on mac
		//FIXME pathname.isFile() does not have to be checked here?
		return pathname.isFile() && !pathname.getName().startsWith(".") && ImgPriority.priorities.containsKey(mime);
	}
}
