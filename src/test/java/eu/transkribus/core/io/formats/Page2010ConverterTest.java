package eu.transkribus.core.io.formats;

import java.io.File;
import java.io.IOException;

import eu.transkribus.core.io.formats.Page2010Converter;

public class Page2010ConverterTest {
	public static void main(String[] args) throws IOException{
//		Page2010Converter.updatePageFormatSingleFile(
//				new File("/mnt/dea_scratch/tmp_philip/tmp/page_2010_test/071_109_001.xml"), 
//					"/mnt/dea_scratch/tmp_philip/tmp/page_2010_test/bak");
		Page2010Converter.updatePageFormat(new File("/mnt/dea_scratch/tmp_philip/tmp/xml2010to2013/page"), 
				"/mnt/dea_scratch/tmp_philip/tmp/xml2010to2013/page/old");
	}
}
