package de.tum.imomesa.analyzer.helpers;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Formatter {

	public static DecimalFormat FORMAT_MINIMAL = (DecimalFormat) NumberFormat.getInstance();
	public static DecimalFormat FORMAT_GERMAN = (DecimalFormat) NumberFormat.getInstance(Locale.GERMAN);
	public static DecimalFormat FORMAT_ENGLISH = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);

	static {
		FORMAT_MINIMAL.setMinimumFractionDigits(0);
		FORMAT_MINIMAL.setMaximumFractionDigits(0);
	}

}
