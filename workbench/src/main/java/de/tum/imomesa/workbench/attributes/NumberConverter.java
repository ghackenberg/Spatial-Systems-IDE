package de.tum.imomesa.workbench.attributes;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javafx.util.StringConverter;

public class NumberConverter extends StringConverter<Number> {

	@Override
	public String toString(Number object) {
		return Double.toString(round(object.doubleValue(), 2));
	}

	@Override
	public Number fromString(String string) {

		// "-" as 0 
		if(string.equals("-")) {
			return 0.;
		}
		
		// empty string
		if(string.isEmpty()) {
			return 0.;
		}
		
		try {
			return Double.parseDouble(string);
		}
		catch(NumberFormatException e) {
			// error parsing
			throw new IllegalStateException();
		}
	}
	
	private static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

}
