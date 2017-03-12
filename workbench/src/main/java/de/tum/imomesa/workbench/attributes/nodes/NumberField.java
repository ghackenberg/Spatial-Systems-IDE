package de.tum.imomesa.workbench.attributes.nodes;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javafx.scene.control.TextField;

public class NumberField extends TextField {

    @Override
    public void replaceText(int start, int end, String text)
    {
    	String s = getText();
    	if(start == end) {
    		s = s.substring(0, start) + text + s.substring(end, s.length());
    	}
    	else if(start < end) {
    		s = s.substring(0, start) + s.substring(end, s.length());
    	}
    	
        if (validate(s))
        {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text)
    {
    	if (validate(text))
        {
            super.replaceSelection(text);
        }
    }

    private boolean validate(String value)
    {
    	if (value.length() == 0) {
    		return true;
    	}
	 
    	// check if more than one dot
    	String[] commaSeperated = value.split("\\.");
    	if (commaSeperated.length > 2) {
    		return false;
    	}
    	
    	// check if value has more than two numbers after comma
    	if(commaSeperated.length == 2) {
    		if(commaSeperated[1].length() > 2) {
    			return false;
    		}
    	}
    	
    	// check if value contains comma (not allowed)
    	if (value.contains(",")) {
    		return false;
    	}
    	
    	// check if value is only "-" -> checked by NumberConverter
    	if (value.equals("-")) {
    		return true;
    	}
	 
    	try {
    		NumberFormat format = NumberFormat.getInstance(Locale.US);
    	    Number number = format.parse(value);
    	    number.doubleValue();
    		return true;
    	} catch (NumberFormatException | ParseException ex) {
    		return false;
    	}
    }
}