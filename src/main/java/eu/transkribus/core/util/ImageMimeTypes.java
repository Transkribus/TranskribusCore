package eu.transkribus.core.util;

/*******************************************************************************
 * Copyright (c) 2013 DEA.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     DEA - initial API and implementation
 ******************************************************************************/

import java.util.HashMap;
import java.util.Map;

/**
 * Taken from package org.dea.imagestore.img;
 * 
 * Based on lookup routine Copyright (c) 2003-2009, Jodd Team (jodd.org). All
 * Rights Reserved.
 * 
 * Map file extensions to MIME types. Based on the Apache mime.types file.
 * http://www.iana.org/assignments/media-types/
 */
public class ImageMimeTypes {
	public static final String MIME_IMAGE_BMP = "image/bmp";
	public static final String MIME_IMAGE_CGM = "image/cgm";
	public static final String MIME_IMAGE_GIF = "image/gif";
	public static final String MIME_IMAGE_IEF = "image/ief";
	public static final String MIME_IMAGE_JPEG = "image/jpeg";
	public static final String MIME_IMAGE_JPEG2000 = "image/jp2";
	public static final String MIME_IMAGE_TIFF = "image/tiff";
	public static final String MIME_IMAGE_PNG = "image/png";
	public static final String MIME_IMAGE_SVG_XML = "image/svg+xml";
	public static final String MIME_IMAGE_VND_DJVU = "image/vnd.djvu";
	public static final String MIME_IMAGE_WAP_WBMP = "image/vnd.wap.wbmp";
	public static final String MIME_IMAGE_X_CMU_RASTER = "image/x-cmu-raster";
	public static final String MIME_IMAGE_X_ICON = "image/x-icon";
	public static final String MIME_IMAGE_X_PORTABLE_ANYMAP = "image/x-portable-anymap";
	public static final String MIME_IMAGE_X_PORTABLE_BITMAP = "image/x-portable-bitmap";
	public static final String MIME_IMAGE_X_PORTABLE_GRAYMAP = "image/x-portable-graymap";
	public static final String MIME_IMAGE_X_PORTABLE_PIXMAP = "image/x-portable-pixmap";
	public static final String MIME_IMAGE_X_RGB = "image/x-rgb";

	private static HashMap<String, String> mimeTypeMapping;

	static {
		mimeTypeMapping = new HashMap<String, String>(200);

		registerMimeType(MIME_IMAGE_X_RGB, "rgb");
		registerMimeType(MIME_IMAGE_X_PORTABLE_PIXMAP, "ppm");
		registerMimeType(MIME_IMAGE_X_PORTABLE_GRAYMAP, "pgm");
		registerMimeType(MIME_IMAGE_X_PORTABLE_BITMAP, "pbm");
		registerMimeType(MIME_IMAGE_X_PORTABLE_ANYMAP, "pnm");
		registerMimeType(MIME_IMAGE_X_ICON, "ico");
		registerMimeType(MIME_IMAGE_X_CMU_RASTER, "ras");
		registerMimeType(MIME_IMAGE_WAP_WBMP, "wbmp");
		registerMimeType(MIME_IMAGE_VND_DJVU, "djvu djv");
		registerMimeType(MIME_IMAGE_SVG_XML, "svg");
		registerMimeType(MIME_IMAGE_IEF, "ief");
		registerMimeType(MIME_IMAGE_CGM, "cgm");
		registerMimeType(MIME_IMAGE_BMP, "bmp");
		registerMimeType(MIME_IMAGE_GIF, "gif");
		registerMimeType(MIME_IMAGE_JPEG, "jpg jpeg jpe");
		registerMimeType(MIME_IMAGE_JPEG2000, "jp2 j2k jpf jpg2");
		registerMimeType(MIME_IMAGE_TIFF, "tiff tif");
		registerMimeType(MIME_IMAGE_PNG, "png");
	}

	public static boolean isMimeTypeBrowserCompatible(String mimeType) {
		return (mimeType.equals(MIME_IMAGE_JPEG) || mimeType.equals(MIME_IMAGE_PNG));
	}

	public static void registerMimeType(String mimeType, String extensions) {
		if (mimeTypeMapping.get(mimeType) != null)
			throw new IllegalArgumentException("Duplicated content type: " + mimeType);

		mimeTypeMapping.put(mimeType, extensions);
	}

	/**
	 * Simply returns MIME type or <code>null</code> if no type is found.
	 */
	public static String getMimeType(String ext) {
		if (ext == null)
			return null;

		for (Map.Entry<String, String> e : mimeTypeMapping.entrySet()) {
			String key = e.getKey();
			String value = e.getValue();

			String[] exts = value.split(" ");
			for (String token : exts)
				if (token.equals(ext))
					return key;
		}
		return null;
	}

	/**
	 * Returns the extension for a given mime type or <code>null</code> if not
	 * found.
	 */
	public static String getExtension(String mimeType) {
		if (mimeType == null)
			return null;

		String exts = mimeTypeMapping.get(mimeType);
		if (exts == null)
			return null;

		String[] tokens = exts.split(" ");
		return tokens[0];
	}

}
