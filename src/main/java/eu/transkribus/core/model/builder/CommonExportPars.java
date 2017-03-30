package eu.transkribus.core.model.builder;

import java.util.Set;

import eu.transkribus.core.model.builder.tei.TeiExportPars;
import eu.transkribus.core.util.GsonUtil;

/**
 * A general set of export parameters. Can and shall be subclassed for special exports as e.g. in {@link TeiExportPars}
 */
public class CommonExportPars {
	boolean writeTextOnWordLevel = false;
	boolean doBlackening = false;
	Set<Integer> pageIndices = null;
	Set<String> selectedTags = null;
			
	public CommonExportPars() {
	}
	
	public static void main(String[] args) {
		System.out.println(GsonUtil.toJson(new CommonExportPars()));
	}

	public boolean isWriteTextOnWordLevel() {
		return writeTextOnWordLevel;
	}

	public void setWriteTextOnWordLevel(boolean writeTextOnWordLevel) {
		this.writeTextOnWordLevel = writeTextOnWordLevel;
	}

	public boolean isDoBlackening() {
		return doBlackening;
	}

	public void setDoBlackening(boolean doBlackening) {
		this.doBlackening = doBlackening;
	}

	public Set<Integer> getPageIndices() {
		return pageIndices;
	}

	public void setPageIndices(Set<Integer> pageIndices) {
		this.pageIndices = pageIndices;
		
		if (this.pageIndices != null && this.pageIndices.isEmpty()) // if no pages -> set to null -> means all pages in tei export
			this.pageIndices = null;
	}

	public Set<String> getSelectedTags() {
		return selectedTags;
	}

	public void setSelectedTags(Set<String> selectedTags) {
		this.selectedTags = selectedTags;
	}
	
	// utility method:
	public boolean isTagSelected(String tagName) {
		return selectedTags == null || selectedTags.contains(tagName);
	}

	@Override
	public String toString() {
		return "CommonExportPars [writeTextOnWordLevel=" + writeTextOnWordLevel + ", doBlackening=" + doBlackening
				+ ", pageIndices=" + pageIndices + ", selectedTags=" + selectedTags + "]";
	}

	

	
}
