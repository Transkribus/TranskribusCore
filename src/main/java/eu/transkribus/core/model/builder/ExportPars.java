package eu.transkribus.core.model.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.transkribus.core.model.builder.tei.TeiExportPars;
import eu.transkribus.core.util.GsonUtil;

/**
 * A general set of export parameters. Can and shall be subclassed for special exports as e.g. in {@link TeiExportPars}
 */
public class ExportPars {
	public boolean writeTextOnWordLevel = false;
	public boolean doBlackening = false;
	public Set<Integer> pageIndices = null;
	public Set<String> selectedTags = null;
	
	TeiExportPars teiPars = new TeiExportPars();
	
	Map<String, String> altoPars = new HashMap<>();
	
	public ExportPars() {
		altoPars.put("whataver", "value");
	}
	
	public static void main(String[] args) {
		System.out.println(GsonUtil.toJson(new ExportPars()));
	}

	
}
