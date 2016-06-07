package dataobjects;

import java.io.IOException;

public class NSAtoDNA {
	public static String buildDNA(String nsaString) {
		NSA nsa = null;
		try {
			nsa = new NSA(nsaString);
		} catch (NumberFormatException | IOException e) {
			// TODO Well, something.
			e.printStackTrace();
		}
		String output = nsa.convertToDNA();
		return output;
	}
}
