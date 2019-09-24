package eu.transkribus.core.io.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.dea.fimagestore.core.util.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImgPriority {
	private static final Logger logger = LoggerFactory.getLogger(ImgPriority.class);
	
	// this map will be used to prioritize different images for one page on the path
	private static Map<String, Integer> priorities = new HashMap<>();

	static {
		//jpg > tif/tiff
		priorities.put(MimeTypes.MIME_IMAGE_JPEG, 3);
		priorities.put(MimeTypes.MIME_IMAGE_PNG, 2);
		priorities.put(MimeTypes.MIME_IMAGE_TIFF, 1);
	}

	public static Integer getPriority(File f) {
		return getPriority(f.getName());
	}
	
	public static Integer getPriority(String filename) {
		final String mime = MimeTypes.getMimeType(FilenameUtils.getExtension(filename));
		return priorities.get(mime);
	}
	
	public static boolean containsMimeType(final String mimeType) {
		return priorities.containsKey(mimeType);
	}
	
	public static List<String> getAllowedFilenameExtensions() {
		List<String> exts = new ArrayList<>(ImgPriority.priorities.size() * 2);
		for(String mimeType : ImgPriority.priorities.keySet()) {
			List<String> extsForType = MimeTypes.lookupExtensions(mimeType);
			if(extsForType.isEmpty()) {
				logger.error("Reverse lookup of registered mimetype failed: " + mimeType);
				continue;
			}
			for(String ext : extsForType) {
				exts.add(ext.toLowerCase());
				exts.add(ext.toUpperCase());
			}
		}
		return exts;
	}
}
