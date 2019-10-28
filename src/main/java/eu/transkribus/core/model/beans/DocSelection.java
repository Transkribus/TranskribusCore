package eu.transkribus.core.model.beans;

import java.io.IOException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.model.beans.enums.EditStatus;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.PagesStrUtil;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DocSelection {
	private int docId=-1;
	private String pages=null;
	
	// TODO
	private EditStatus editStatus=null; // note: not supported on server yet - editStatus is set globally for all documents!
	Boolean skipPagesWithMissingStatus=null; // note: not supported on server yet - editStatus is set globally for all documents!
	
	public DocSelection() {
	}
	
	public DocSelection(int docId, String pages, EditStatus editStatus,
			Boolean skipPagesWithMissingStatus) {
		super();
		this.docId = docId;
		this.pages = pages;
		this.editStatus = editStatus;
		this.skipPagesWithMissingStatus = skipPagesWithMissingStatus;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public String getPages() {
		return pages;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	public EditStatus getEditStatus() {
		return editStatus;
	}

	public void setEditStatus(EditStatus editStatus) {
		this.editStatus = editStatus;
	}
	
	public boolean getSkipPagesWithMissingStatusOrDefault() {
		return skipPagesWithMissingStatus == null ? false : skipPagesWithMissingStatus;
	}

	public Boolean getSkipPagesWithMissingStatus() {
		return skipPagesWithMissingStatus;
	}

	public void setSkipPagesWithMissingStatus(Boolean skipPagesWithMissingStatus) {
		this.skipPagesWithMissingStatus = skipPagesWithMissingStatus;
	}

	// helper methods:
	public int getNrOfSelectedPages(int nOfPagesInDoc) throws IOException {
		return pages==null ? nOfPagesInDoc : PagesStrUtil.getPageNrs(pages, nOfPagesInDoc, false).size(); 
	}
	

	@Override
	public String toString() {
		String str = "DocSelection [docId=" + docId + ", pages=" + pages;
		if (editStatus != null) {
			str += ", editStatus=" + editStatus+", skipPagesWithMissingStatus=" + skipPagesWithMissingStatus;
		}
		str += "]";
		return str;
	}
	
	public static void main(String[] args) throws Exception {
		DocSelection d = new DocSelection(10, "1-20", EditStatus.FINAL, true);
		System.out.println(JaxbUtils.marshalToString(d));
		
		System.out.println(JaxbUtils.marshalToString(new DocSelection()));
	}
	
}
