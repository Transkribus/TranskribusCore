package eu.transkribus.core.io.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.dea.fimagestore.core.util.MimeTypes;

public class ImgPriority {
	// this map will be used to prioritize different images for one page on the path
	public static Map<String, Integer> priorities = new HashMap<>();

	static {
		//jpg > tif/tiff
		priorities.put(MimeTypes.MIME_IMAGE_JPEG, 3);
		priorities.put(MimeTypes.MIME_IMAGE_PNG, 2);
		priorities.put(MimeTypes.MIME_IMAGE_TIFF, 1);
	}

	public static Integer getPriority(File f) {
		final String mime = MimeTypes.getMimeType(FilenameUtils.getExtension(f.getName()));
		return priorities.get(mime);
	}
}
