package eu.transkribus.core.io;

import java.net.URL;

public class LocalDocConst {
	public static String URL_PROT_CONST = "file://";
	public static final String PAGE_FILE_SUB_FOLDER = "page";
	public static final String OCR_FILE_SUB_FOLDER = "ocr";
	public static final String OCR_MASTER_DIR = "OCRmaster";
	public static final String FEP_IMG_DIR = "img";
	public static final String ALTO_FILE_SUB_FOLDER = "alto";
	public static final String TXT_FILE_SUB_FOLDER = "txt";
	public static final String THUMBS_FILE_SUB_FOLDER = "thumbs";
	public static final int THUMB_SIZE_HEIGHT = 120;
	public static final String THUMB_FILE_EXT = ".jpg";
	public static final String EDITORIAL_DECLARATION_FN = "editorialDeclaration.xml";
	public static final String METADATA_FILENAME = "metadata.xml";
	public static final String NO_IMAGE_FILENAME = "NO_IMAGE";
	private static final String MISSING_IMAGE_NAME = "image_unavailable.png";
	
	public static URL getDummyImageUrl() {
		return LocalDocConst.class.getClassLoader().getResource(MISSING_IMAGE_NAME);
	}
}
