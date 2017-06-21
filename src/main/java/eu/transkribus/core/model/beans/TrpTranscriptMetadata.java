package eu.transkribus.core.model.beans;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.dea.fimgstoreclient.utils.FimgStoreUtils;

import eu.transkribus.core.exceptions.NullValueException;
import eu.transkribus.core.model.beans.adapters.EditStatusAdapter;
import eu.transkribus.core.model.beans.enums.EditStatus;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.PageXmlUtils;

@Entity
@Table(name = "transcripts")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpTranscriptMetadata implements ITrpFile, Serializable, Comparable<TrpTranscriptMetadata> {
	private static final long serialVersionUID = 1L;
	
	static DateFormat timeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	TrpPage pageReferenceForLocalDocs;
	
	@Id
	@Column(name = "TSID")
	private int tsId = -1;
	
	@Column(name = "PARENT_TSID")
	private int parentTsId = -1;
	
	@Column(name = "xmlkey")
	private String key = null; //the fimagestore key for getting the XML
	
	@Column
	private int pageId = -1;
	
	@Transient
	@Column
	private int docId = -1;
	
	@Transient
	@Column
	private int pageNr = -1;
	
	@Transient
	private File localFolder = null; // != null when local document
	
	@Column
	@Transient
	private URL url;
	
	@Column
	@XmlJavaTypeAdapter(EditStatusAdapter.class)
	private EditStatus status;
	@Column(name="userId")
	private String userName;
	@Column(name="user_id")
	private int userId;
//	@XmlJavaTypeAdapter(DateAdapter.class)
//	private Date time = new Date();
	@Column
	private long timestamp = System.currentTimeMillis();
	
	@Column
	private String toolName = null;
	
	@Column(name="NOTE")
	private String note = "";
	
	private String md5Sum = "";

	@Column(name="NR_OF_REGIONS")
	private int nrOfRegions;

	@Column(name="NR_OF_TRANSCRIBED_REGIONS")
	private int nrOfTranscribedRegions;

	@Column(name="NR_OF_WORDS_IN_REGIONS")
	private int nrOfWordsInRegions;
	
	@Column(name="NR_OF_LINES")
	private int nrOfLines;

	@Column(name="NR_OF_TRANSCRIBED_LINES")
	private int nrOfTranscribedLines;

	@Column(name="NR_OF_WORDS_IN_LINES")
	private int nrOfWordsInLines;

	@Column(name="NR_OF_WORDS")
	private int nrOfWords;

	@Column(name="NR_OF_TRANSCRIBED_WORDS")
	private int nrOfTranscribedWords;
	
	//TODO tags
	//TODO annotations

	public TrpTranscriptMetadata() {
	}
	
	
	
	public TrpTranscriptMetadata(final int tsId, final String key, final int pageId, 
			final long timestamp, final int userId, final String userName, final EditStatus status, final int parentId, final String note) {
		this.tsId = tsId;
		this.key = key;
		this.pageId = pageId;
		this.timestamp = timestamp;
		this.userId = userId;
		this.userName = userName;
		this.status = status;
		this.parentTsId = parentId;
		this.note = note;
	}

	public TrpTranscriptMetadata(TrpTranscriptMetadata m, TrpPage pageReferenceForLocalDocs) {
		this();
		this.pageReferenceForLocalDocs = pageReferenceForLocalDocs;
		tsId = m.getTsId();
		parentTsId = m.getParentTsId();
		key = m.getKey();
		pageId = m.getPageId();
		docId = m.getDocId();
		pageNr = m.getPageNr();
		localFolder = m.getLocalFolder();
		url = m.getUrl();
		status = m.getStatus();
		userName = m.getUserName();
		userId = m.getUserId();
		timestamp = m.getTimestamp();
		toolName = m.getToolName();
		note = m.getNote();
		md5Sum = m.getMd5Sum();
		nrOfRegions = m.getNrOfRegions();
		nrOfTranscribedRegions = m.getNrOfTranscribedRegions();
		nrOfWordsInRegions = m.getNrOfWordsInRegions();
		nrOfLines = m.getNrOfLines();
		nrOfTranscribedLines = m.getNrOfTranscribedLines();
		nrOfWordsInLines = m.getNrOfWordsInLines();
		nrOfWords = m.getNrOfWords();
		nrOfTranscribedWords = m.getNrOfTranscribedWords();
	}

	public int getTsId() {
		return tsId;
	}

	public void setTsId(int tsId) {
		this.tsId = tsId;
	}

	public int getParentTsId() {
		return parentTsId;
	}

	public void setParentTsId(int parentTsId) {
		this.parentTsId = parentTsId;
	}

	public File getLocalFolder() {
		return localFolder;
	}

	public void setLocalFolder(File localFolder) {
		this.localFolder = localFolder;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String xmlKey) {
		this.key = xmlKey;
	}

	public int getPageId() {
		return pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	
	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public int getPageNr() {
		return pageNr;
	}

	public void setPageNr(int pageNr) {
		this.pageNr = pageNr;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL xmlUrl) {
		this.url = xmlUrl;
	}

	public EditStatus getStatus() {
		return status;
	}

	public void setStatus(EditStatus status) {
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
//	@Deprecated
//	public Date getTime() {
//		return time;
//	}
//
//	@Deprecated
//	public void setTime(Date time) {
//		this.time = time;
//	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public Date getTime() {
		return new Date(this.timestamp);
	}
	
	public String getTimeFormatted() {
		return timeFormatter.format(getTime());
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public void setTime(Date time) {
		this.timestamp = time.getTime();
	}
	
	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}
	
	public String getNote(){
		return note;
	}
	
	public void setNote(String note){
		this.note = note;
	}

	public String getMd5Sum() {
		return this.md5Sum;
	}

	public void setMd5Sum(String md5Sum) {
		this.md5Sum = md5Sum;
	}
	
	public TrpPage getPagePageReferenceForLocalDocs() {
		return pageReferenceForLocalDocs;
	}
	
	public void setPageReferenceForLocalDocs(TrpPage pageReferenceForLocalDocs) {
		this.pageReferenceForLocalDocs = pageReferenceForLocalDocs;
	}
	
	public TrpTranscriptStatistics getStats() {
		TrpTranscriptStatistics s = new TrpTranscriptStatistics();
		s.setNrOfLines(this.nrOfLines);
		s.setNrOfRegions(this.nrOfRegions);
		s.setNrOfTranscribedLines(this.nrOfTranscribedLines);
		s.setNrOfTranscribedRegions(this.nrOfTranscribedRegions);
		s.setNrOfTranscribedWords(this.nrOfTranscribedWords);
		s.setNrOfWords(this.nrOfWords);
		s.setNrOfWordsInLines(this.nrOfWordsInLines);
		s.setNrOfWordsInRegions(this.nrOfWordsInRegions);
		return s;
	}
	
	public void setStats(TrpTranscriptStatistics s) {
		this.nrOfLines = s.getNrOfLines();
		this.nrOfRegions = s.getNrOfRegions();
		this.nrOfTranscribedLines = s.getNrOfTranscribedLines();
		this.nrOfTranscribedRegions = s.getNrOfTranscribedRegions();
		this.nrOfTranscribedWords = s.getNrOfTranscribedWords();
		this.nrOfWords = s.getNrOfWords();
		this.nrOfWordsInLines = s.getNrOfWordsInLines();
		this.nrOfWordsInRegions = s.getNrOfWordsInRegions();
	}

	public int getNrOfRegions() {
		return nrOfRegions;
	}
	public void setNrOfRegions(int nrOfRegions) {
		this.nrOfRegions = nrOfRegions;
	}
	public int getNrOfTranscribedRegions() {
		return nrOfTranscribedRegions;
	}
	public void setNrOfTranscribedRegions(int nrOfTranscribedRegions) {
		this.nrOfTranscribedRegions = nrOfTranscribedRegions;
	}
	public int getNrOfWordsInRegions() {
		return nrOfWordsInRegions;
	}
	public void setNrOfWordsInRegions(int nrOfWordsInRegions) {
		this.nrOfWordsInRegions = nrOfWordsInRegions;
	}
	public int getNrOfLines() {
		return nrOfLines;
	}
	public void setNrOfLines(int nrOfLines) {
		this.nrOfLines = nrOfLines;
	}
	public int getNrOfTranscribedLines() {
		return nrOfTranscribedLines;
	}
	public void setNrOfTranscribedLines(int nrOfTranscribedLines) {
		this.nrOfTranscribedLines = nrOfTranscribedLines;
	}
	public int getNrOfWordsInLines() {
		return nrOfWordsInLines;
	}
	public void setNrOfWordsInLines(int nrOfWordsInLines) {
		this.nrOfWordsInLines = nrOfWordsInLines;
	}
	public int getNrOfWords() {
		return nrOfWords;
	}
	public void setNrOfWords(int nrOfWords) {
		this.nrOfWords = nrOfWords;
	}
	public int getNrOfTranscribedWords() {
		return nrOfTranscribedWords;
	}
	public void setNrOfTranscribedWords(int nrOfTranscribedWords) {
		this.nrOfTranscribedWords = nrOfTranscribedWords;
	}	
	
	/**
	 * Check key and URL protocol.<br/>
	 * This method returns false, if there is a filekey set which matches the pattern defined in FimagestoreClient.<br/>
	 * It returns true, if there is no filekey and the URL points to an existing file.<br/>
	 * 
	 * @return
	 * @throws IllegalStateException in case there are inconsistencies:
	 * <ul>
	 * <li>key of transcript is null, but the URL protocol is not "file://"</li>
	 * <li>key of transcript is null, but the file URL does not point to an existing file</li>
	 * <li>a filekey is set, but it does not match the fimagestore key pattern</li>
	 * </ul>
	 */
	public boolean isLocalTranscript() {
		if(key == null) {
			final String prot = url.getProtocol();
			if(!"file".equals(prot)) {
				throw new IllegalStateException("Key of tanscript is null, but URL protocol is not file!");
			}
			if(!new File(url.getFile()).isFile()) {
				throw new IllegalStateException("Key of tanscript is null, but file URL does not exis: " + url.getFile());			}
			return true;
		} else {
			if(!FimgStoreUtils.isFimgStoreKey(key)) {
				throw new IllegalStateException("Key of tanscript is not a valid fimagestore key!");
			}
			return false;
		}
	}
	
	@Override 
	public boolean equals(Object o) {
		// FIXME ?? (not tested)
		
		if (o==null || !(o instanceof TrpTranscriptMetadata))
			return false;
		
		TrpTranscriptMetadata m = (TrpTranscriptMetadata) o;
		if (pageId != m.pageId)
			return false;
		if (!CoreUtils.equalsObjects(key, m.key))
			return false;
		if (!CoreUtils.equalsObjects(localFolder, m.localFolder))
			return false;
		
		int c = compareTo(m);
		if (c != 0)
			return false;
		
		return true;
	}
	
	/**
	 * Uses the timestamp for comparison
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TrpTranscriptMetadata md) {
		int result = 0;
		if (this.getTimestamp() > md.getTimestamp()) {
			result = 1;
		} else if (this.getTimestamp() < md.getTimestamp()) {
			result = -1;
		}
		return result;
	}

	/**
	 * This method is just for testing equivalence of documents selected via different DocManager methods
	 * @param obj
	 * @return
	 */
	public boolean testEquals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrpTranscriptMetadata other = (TrpTranscriptMetadata) obj;
		if (docId != other.docId)
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (localFolder == null) {
			if (other.localFolder != null)
				return false;
		} else if (!localFolder.equals(other.localFolder))
			return false;
		if (md5Sum == null) {
			if (other.md5Sum != null)
				return false;
		} else if (!md5Sum.equals(other.md5Sum))
			return false;
		if (note == null) {
			if (other.note != null)
				return false;
		} else if (!note.equals(other.note))
			return false;
		if (nrOfLines != other.nrOfLines)
			return false;
		if (nrOfRegions != other.nrOfRegions)
			return false;
		if (nrOfTranscribedLines != other.nrOfTranscribedLines)
			return false;
		if (nrOfTranscribedRegions != other.nrOfTranscribedRegions)
			return false;
		if (nrOfTranscribedWords != other.nrOfTranscribedWords)
			return false;
		if (nrOfWords != other.nrOfWords)
			return false;
		if (nrOfWordsInLines != other.nrOfWordsInLines)
			return false;
		if (nrOfWordsInRegions != other.nrOfWordsInRegions)
			return false;
		if (pageId != other.pageId)
			return false;
		if (pageNr != other.pageNr)
			return false;
		if (pageReferenceForLocalDocs == null) {
			if (other.pageReferenceForLocalDocs != null)
				return false;
		} else if (!pageReferenceForLocalDocs.equals(other.pageReferenceForLocalDocs))
			return false;
		if (parentTsId != other.parentTsId)
			return false;
		if (status != other.status)
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (toolName == null) {
			if (other.toolName != null)
				return false;
		} else if (!toolName.equals(other.toolName))
			return false;
		if (tsId != other.tsId)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (userId != other.userId)
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	

	public PcGtsType unmarshallTranscript() throws NullValueException, JAXBException {
		if (getUrl()==null)
			throw new NullValueException("URL of transcript is null!");
				
		return PageXmlUtils.unmarshal(getUrl());	
	}

//	@Override
//	public int compareTo(TrpTranscriptMetadata md) {
//		if (md.getTime() == null && this.getTime() == null) {
//			return 0;
//		}
//		if (this.getTime() == null) {
//			return 1;
//		}
//		if (md.getTime() == null) {
//			return -1;
//		}
//		return this.getTime().compareTo(md.getTime());
//	}
	
	
	
////	@Override
//	public String toString2(){
//		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName() + " {");
//		sb.append(this.getTsId() + " - ");
//		sb.append(this.getPageId() + " - ");
//		sb.append(this.getStatus() + " - ");
//		sb.append(this.getKey() + " - ");
//		sb.append((new Date(this.getTimestamp())).toString() + " - ");
//		sb.append(this.getUserName() + " - ");
//		sb.append(this.getUrl() + "}");
//		return sb.toString();
//	}

	@Override
	public String toString() {
		return "TrpTranscriptMetadata [tsId=" + tsId
				+ ", parentTsId=" + parentTsId + ", key=" + key + ", pageId=" + pageId + ", docId=" + docId
				+ ", pageNr=" + pageNr + ", localFolder=" + localFolder + ", url=" + url + ", status=" + status
				+ ", userName=" + userName + ", userId=" + userId + ", timestamp=" + timestamp + ", toolName="
				+ toolName + ", note=" + note + ", md5Sum=" + md5Sum + ", nrOfRegions=" + nrOfRegions
				+ ", nrOfTranscribedRegions=" + nrOfTranscribedRegions + ", nrOfWordsInRegions=" + nrOfWordsInRegions
				+ ", nrOfLines=" + nrOfLines + ", nrOfTranscribedLines=" + nrOfTranscribedLines + ", nrOfWordsInLines="
				+ nrOfWordsInLines + ", nrOfWords=" + nrOfWords + ", nrOfTranscribedWords=" + nrOfTranscribedWords
				+ "]";
	}
	
	public class TrpTranscriptStatistics {
		private int nrOfRegions;
		private int nrOfTranscribedRegions;
		private int nrOfWordsInRegions;
		private int nrOfLines;
		private int nrOfTranscribedLines;
		private int nrOfWordsInLines;
		private int nrOfWords;
		private int nrOfTranscribedWords;
		public int getNrOfRegions() {
			return nrOfRegions;
		}
		public void setNrOfRegions(int nrOfRegions) {
			this.nrOfRegions = nrOfRegions;
		}
		public int getNrOfTranscribedRegions() {
			return nrOfTranscribedRegions;
		}
		public void setNrOfTranscribedRegions(int nrOfTranscribedRegions) {
			this.nrOfTranscribedRegions = nrOfTranscribedRegions;
		}
		public int getNrOfWordsInRegions() {
			return nrOfWordsInRegions;
		}
		public void setNrOfWordsInRegions(int nrOfWordsInRegions) {
			this.nrOfWordsInRegions = nrOfWordsInRegions;
		}
		public int getNrOfLines() {
			return nrOfLines;
		}
		public void setNrOfLines(int nrOfLines) {
			this.nrOfLines = nrOfLines;
		}
		public int getNrOfTranscribedLines() {
			return nrOfTranscribedLines;
		}
		public void setNrOfTranscribedLines(int nrOfTranscribedLines) {
			this.nrOfTranscribedLines = nrOfTranscribedLines;
		}
		public int getNrOfWordsInLines() {
			return nrOfWordsInLines;
		}
		public void setNrOfWordsInLines(int nrOfWordsInLines) {
			this.nrOfWordsInLines = nrOfWordsInLines;
		}
		public int getNrOfWords() {
			return nrOfWords;
		}
		public void setNrOfWords(int nrOfWords) {
			this.nrOfWords = nrOfWords;
		}
		public int getNrOfTranscribedWords() {
			return nrOfTranscribedWords;
		}
		public void setNrOfTranscribedWords(int nrOfTranscribedWords) {
			this.nrOfTranscribedWords = nrOfTranscribedWords;
		}
	}
}
