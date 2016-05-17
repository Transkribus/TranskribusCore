package eu.transkribus.core.model.builder;

import java.util.Set;

import eu.transkribus.core.model.builder.tei.TeiExportPars;

/**
 * A general set of export parameters. Can and shall be subclassed for special exports as e.g. in {@link TeiExportPars}
 */
public class ExportPars {
	public boolean writeTextOnWordLevel = false;
	public boolean doBlackening = false;
	public Set<Integer> pageIndices = null;
	public Set<String> selectedTags = null;

	
}
