package eu.transkribus.core.io.formats;

import java.io.File;
import java.io.IOException;

import eu.transkribus.core.io.formats.Page2010Converter;

public class Page2010ConverterTest {
	public static void main(String[] args) throws IOException{
		Page2010Converter.updatePageFormatSingleFile(new File("/tmp/2.xml"), "/tmp/test-bak");
	}
}
