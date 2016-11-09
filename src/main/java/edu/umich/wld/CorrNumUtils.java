package edu.umich.wld;

import java.util.regex.Pattern;

public class CorrNumUtils {
	
	// from Java Double documentation
	public static boolean isParsableAsDouble(String value) {
		final String digits = "(\\p{Digit}+)";
		final String hexDigits = "(\\p{XDigit}+)";
		// an exponent is 'e' or 'E' followed by an optionally signed decimal integer.
		final String exp = "[eE][+-]?" + digits;
		final String regex = (
				"[\\x00-\\x20]*" +   // Optional leading "whitespace"
				"[+-]?(" + 			// Optional sign character
				"NaN|" +             // "NaN" string
				"Infinity|" +        // "Infinity" string

				// A decimal floating-point string representing a finite positive
				// number without a leading sign has at most five basic pieces:
				// Digits . Digits ExponentPart FloatTypeSuffix
				
				// Since this method allows integer-only strings as input
				// in addition to strings of floating-point literals, the
				// two sub-patterns below are simplifications of the grammar
				// productions from the Java Language Specification, 2nd 
				// edition, section 3.10.2.

				// Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
				"(((" + digits + "(\\.)?(" + digits + "?)(" + exp + ")?)|" +

				// . Digits ExponentPart_opt FloatTypeSuffix_opt
				"(\\.(" + digits + ")(" + exp + ")?)|"+

				// Hexadecimal strings
				"((" +
				// 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
				"(0[xX]" + hexDigits + "(\\.)?)|" +

				// 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
				"(0[xX]" + hexDigits + "?(\\.)" + hexDigits + ")" +

				")[pP][+-]?" + digits + "))" +
				"[fFdD]?))" +
				"[\\x00-\\x20]*" // Optional trailing "whitespace"
	    ); 
	          
		return Pattern.matches(regex, value);
	}
}
