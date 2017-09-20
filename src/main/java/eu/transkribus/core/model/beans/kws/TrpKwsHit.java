package eu.transkribus.core.model.beans.kws;

import java.net.URL;

public class TrpKwsHit implements Comparable<TrpKwsHit> {
	double confidence;
	String bl;
	String lineId;
	URL imgUrl;
	int pageId;
	int pageNr;
	int docId;
	int colId;
	
	
	public double getConfidence() {
		return this.confidence;
	}
	@Override
    public int compareTo(TrpKwsHit o) {
        return Double.compare(o.getConfidence(), this.confidence);
    }
}
