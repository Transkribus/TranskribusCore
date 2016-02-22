package eu.transkribus.core.model.beans;

public class WordHypothesis {
	private int ti;
	private int tf;
	private String word;
	private double confidence;
	
	public WordHypothesis(int ti, int tf, String word, double confidence){
		this.ti = ti;
		this.tf = tf;
		this.word = word;
		this.confidence = confidence;
	}

	public int getTi() {
		return ti;
	}

	public void setTi(int ti) {
		this.ti = ti;
	}

	public int getTf() {
		return tf;
	}

	public void setTf(int tf) {
		this.tf = tf;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
}