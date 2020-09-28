package eu.transkribus.core.util;

import static eu.transkribus.core.util.CoreUtils.join;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringEscapeUtils;

public class CSVUtils {
	public static final char DEFAULT_SEPARATOR = ',';
	
	public static void writeLineWoEncoding(Writer w, String line) throws IOException {
		w.append(line+"\n");
	}
	
	public static void writeLine(Writer w, String... vals) throws IOException {
		String line = join(Arrays.asList(vals).stream().map(s -> StringEscapeUtils.escapeCsv(s)).collect(Collectors.toList()));
		w.write(line+"\n");
	}
}