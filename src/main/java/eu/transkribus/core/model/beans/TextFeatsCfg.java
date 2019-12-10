package eu.transkribus.core.model.beans;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.util.GsonUtil;
import eu.transkribus.core.util.JaxbUtils;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TextFeatsCfg {
	boolean verbose = false; // Whether to be verbose
	boolean deslope = true; // Whether to do automatic desloping of the text
	boolean deslant = true; // Whether to do automatic deslanting of the text
	String type = "raw"; // Type of feature to extract, either "dotm" or "raw"
	String format = "img"; // Output features format, either "htk", "ascii" or "img"
	boolean stretch = true; // Whether to do contrast stretching
	boolean enh = true; //
	int enh_win = 30; // Window size in pixels for local enhancement
	double enh_prm = 0.2; // Sauvola enhancement parameter
	//enh_prm = [ 0.05, 0.2, 0.5 ]; // 3 independent enhancements, each in a color channel
	int normheight = 0; // Normalize image heights
	int normxheight = 0; //
	boolean momentnorm = true; // Global line vertical moment normalization
	boolean fpgram = false; // Whether to compute the features parallelograms
	boolean fcontour = true; // Whether to compute the features surrounding polygon
	int fcontour_dilate = 0; //
	int padding = 10; // Padding in pixels to add to the left and right
	
	public TextFeatsCfg() {
	}
	
	public boolean isVerbose() {
		return verbose;
	}
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	public boolean isDeslope() {
		return deslope;
	}
	public void setDeslope(boolean deslope) {
		this.deslope = deslope;
	}
	public boolean isDeslant() {
		return deslant;
	}
	public void setDeslant(boolean deslant) {
		this.deslant = deslant;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public boolean isStretch() {
		return stretch;
	}
	public void setStretch(boolean stretch) {
		this.stretch = stretch;
	}
	public boolean isEnh() {
		return enh;
	}
	public void setEnh(boolean enh) {
		this.enh = enh;
	}
	public int getEnh_win() {
		return enh_win;
	}
	public void setEnh_win(int enh_win) {
		this.enh_win = enh_win;
	}
	public double getEnh_prm() {
		return enh_prm;
	}
	public void setEnh_prm(double enh_prm) {
		this.enh_prm = enh_prm;
	}
	public int getNormheight() {
		return normheight;
	}
	public void setNormheight(int normheight) {
		this.normheight = normheight;
	}
	public int getNormxheight() {
		return normxheight;
	}
	public void setNormxheight(int normxheight) {
		this.normxheight = normxheight;
	}
	public boolean isMomentnorm() {
		return momentnorm;
	}
	public void setMomentnorm(boolean momentnorm) {
		this.momentnorm = momentnorm;
	}
	public boolean isFpgram() {
		return fpgram;
	}
	public void setFpgram(boolean fpgram) {
		this.fpgram = fpgram;
	}
	public boolean isFcontour() {
		return fcontour;
	}
	public void setFcontour(boolean fcontour) {
		this.fcontour = fcontour;
	}
	public int getFcontour_dilate() {
		return fcontour_dilate;
	}
	public void setFcontour_dilate(int fcontour_dilate) {
		this.fcontour_dilate = fcontour_dilate;
	}
	public int getPadding() {
		return padding;
	}
	public void setPadding(int padding) {
		this.padding = padding;
	}
	
	/**
	 * Creates the string as required by the textfeats tool 
	 */
	public String toConfigString() {
		String str = "TextFeatExtractor: {\n";
		str += "verbose = "+verbose+";\n";
		str += "deslope = "+deslope+";\n";
		str += "deslant = "+deslant+";\n";
		str += "type = \""+type+"\";\n";
		str += "format = \""+format+"\";\n";
		str += "stretch = "+stretch+";\n";
		str += "enh = "+enh+";\n";
		str += "enh_win = "+enh_win+";\n";
		str += "enh_prm = "+enh_prm+";\n";
		
		str += "normheight = "+normheight+";\n";
		str += "normxheight = "+normxheight+";\n";
		str += "momentnorm = "+momentnorm+";\n";
		
		str += "fpgram = "+fpgram+";\n";
		str += "fcontour = "+fcontour+";\n";
		str += "fcontour_dilate = "+fcontour_dilate+";\n";
		str += "padding = "+padding+";\n";
		str += "}";
		return str;
	}
	
	@Override
	public String toString() {
		return "TextFeatsCfg [verbose=" + verbose + ", deslope=" + deslope + ", deslant=" + deslant + ", type=" + type
				+ ", format=" + format + ", stretch=" + stretch + ", enh=" + enh + ", enh_win=" + enh_win + ", enh_prm="
				+ enh_prm + ", normheight=" + normheight + ", normxheight=" + normxheight + ", momentnorm=" + momentnorm
				+ ", fpgram=" + fpgram + ", fcontour=" + fcontour + ", fcontour_dilate=" + fcontour_dilate
				+ ", padding=" + padding + "]";
	}

	public static void main(String[] args) throws Exception {
		TextFeatsCfg c = new TextFeatsCfg();
		System.out.println("config-str = \n"+c.toConfigString());
	}

}
