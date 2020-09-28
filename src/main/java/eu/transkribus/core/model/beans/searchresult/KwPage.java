package eu.transkribus.core.model.beans.searchresult;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class KwPage{

	@XmlElement(name="pageImg")
	private String image;
	
	@XmlElement(name="pageNr")
	private int pageNr;
	
	@XmlElement(name="transliteration")
	private String transliteration;

	@XmlElement(name="spots")
	private ArrayList<KwPageWord> words;
	
	public KwPage(){
		words = new ArrayList<KwPageWord>();
	}
	
	public ArrayList<KwPageWord> getWords() {
		return words;
	}

	public void setWords(ArrayList<KwPageWord> words) {
		this.words = words;
	}

	public void addWord(KwPageWord w){
		words.add(w);
	}

	public String getImage() {
		return image;
	}

	public void setImage(String img) {
		this.image = img;
	}
		
	public int getPageNr(){
		return pageNr;
	}
	
	public void setPageNr(int nr){
		this.pageNr = nr;
	}
	public String getTransliteration() {
		return transliteration;
	}

	public void setTransliteration(String tr) {
		this.transliteration = tr;
	}
	
}

