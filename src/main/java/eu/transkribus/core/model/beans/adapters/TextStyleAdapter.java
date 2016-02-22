package eu.transkribus.core.model.beans.adapters;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang3.StringUtils;

public class TextStyleAdapter extends XmlAdapter<String, Float> {
	public final static int N_FLOATING_POINT_DECIMAL=1;
	
	public static float roundToDecimals(float value, int nDecimals) {
		final float p = (float) Math.pow(10, nDecimals);
		value = value * p;
		float tmp = Math.round(value);
		return tmp / p;
	}
	
	static DecimalFormat df = new DecimalFormat("#."+StringUtils.repeat('#', N_FLOATING_POINT_DECIMAL));
	static {
		DecimalFormatSymbols customFormatSymbols=new DecimalFormatSymbols();
		customFormatSymbols.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(customFormatSymbols);
		df.setRoundingMode(RoundingMode.HALF_UP);
	}

	/**
	 * Round floating point values (font-size) to one decimal
	 */
	@Override public Float unmarshal(String v) throws Exception {
		float f = Float.valueOf(v);
		float f_rounded = roundToDecimals(f, N_FLOATING_POINT_DECIMAL);
//		System.out.println("unmarshalling "+v+" to "+f_rounded);
		return f_rounded;
	}
	
	/**
	 * Round floating point values (font-size) to one decimal
	 */
	@Override public String marshal(Float v) throws Exception {
		if (v == null)
			return null;
		
		return df.format(v);
//		return Float.toString(roundToDecimals(v.floatValue(), N_FLOATING_POINT_DECIMAL));
	}

	public static void main(String[] args) {
		System.out.println(df.format(1.12334334));
		System.out.println(df.format(34444545331.12));
		System.out.println(df.format(1.155));
		System.out.println(df.format(1.121));
		
		try {
			
			System.out.println(df.parse("561.12334334").floatValue());
			System.out.println(df.parse("1.12").floatValue());
			System.out.println(df.parse("1.125").floatValue());
			System.out.println(df.parse("1.121").floatValue());
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		
	}

}
