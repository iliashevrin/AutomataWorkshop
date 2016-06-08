package dataobjects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class NSAtoDNA {
	private static String [] steps;
	private static int curr_step;
	
	public static String buildDNA(String nsaString) {
		NSA nsa = null;
		try {
			nsa = new NSA(nsaString);
		} catch (NumberFormatException | IOException e) {
			// TODO Well, something.
			e.printStackTrace();
		}
		String steps_str = nsa.convertToDNA();
		
		//Fill "steps" array with step strings
		String [] steps_temp_arr = steps_str.split(NSA.SEPARATOR);
		int num_steps = 0;
		for (int i = 0; i < steps_temp_arr.length; i++) {
			String curr = steps_temp_arr[i];
			if (curr.trim().length() > 0)
				num_steps++;
		}
		steps = new String [num_steps];
		num_steps = 0;
		for (int i = 0; i < steps_temp_arr.length; i++) {
			String curr = steps_temp_arr[i];
			if (curr.trim().length() > 0)
				steps[num_steps++] = curr;
		}		
		curr_step = steps.length - 1;
		return steps[curr_step];
	}
	
	public static String firstStep () {
		curr_step = 0;
		return steps[curr_step];
	}
	
	public static String prevStep () {
		curr_step = (curr_step > 0) ? curr_step - 1 : 0;
		return steps[curr_step];
	}
	
	public static String nextStep () {
		curr_step = (curr_step < steps.length - 1) ? curr_step + 1 : steps.length - 1;
		return steps[curr_step];
	}
	
	public static String lastStep () {
		curr_step = steps.length - 1;
		return steps[curr_step];
	}
	
}
