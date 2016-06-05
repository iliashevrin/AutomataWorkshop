package servlets;

public class Examples {

	public static final String NBA_EXAMPLE_1 = 
			"q0 [label=\"*q0\"]" + System.getProperty("line.separator") +
			"q1 [label=\"*q1$\"]" + System.getProperty("line.separator") +
            "q0 -> q0 [label=0]" + System.getProperty("line.separator") +
            "q0 -> q0 [label=1]" + System.getProperty("line.separator") +
            "q0 -> q1 [label=1]" + System.getProperty("line.separator") +
            "q1 -> q1 [label=1]" + System.getProperty("line.separator");
	
	public static final String NBA_EXAMPLE_2 = 
	        "q0 [label=\"*q0\"]" + System.getProperty("line.separator") +
	        "q1 [label=\"*q1\"]" + System.getProperty("line.separator") +
	        "q2 [label=\"q2$\"]" + System.getProperty("line.separator") +
	        "q0 -> q1 [label=0]" + System.getProperty("line.separator") +
	        "q0 -> q1 [label=1]" + System.getProperty("line.separator") +
	        "q0 -> q1 [label=1]" + System.getProperty("line.separator") +
	        "q1 -> q1 [label=1]" + System.getProperty("line.separator") +
	        "q1 -> q2 [label=0]" + System.getProperty("line.separator") +
	        "q2 -> q2 [label=1]" + System.getProperty("line.separator") +
	        "q2 -> q0 [label=0]" + System.getProperty("line.separator");
	
	public static final String NBA_EXAMPLE_3 = 
	        "q0 [label=\"*q0\"]" + System.getProperty("line.separator") +
	        "q1 [label=\"q1\"]" + System.getProperty("line.separator") +
	        "q2 [label=\"q2$\"]" + System.getProperty("line.separator") +
	        "q3 [label=\"q3$\"]" + System.getProperty("line.separator") +
	        "q4 [label=\"q4\"]" + System.getProperty("line.separator") +
	        "q0 -> q0 [label=0]" + System.getProperty("line.separator") +
	        "q0 -> q0 [label=1]" + System.getProperty("line.separator") +
	        "q0 -> q1 [label=0]" + System.getProperty("line.separator") +
	        "q1 -> q3 [label=0]" + System.getProperty("line.separator") +
	        "q2 -> q1 [label=1]" + System.getProperty("line.separator") +
	        "q2 -> q2 [label=1]" + System.getProperty("line.separator") +
	        "q3 -> q4 [label=1]" + System.getProperty("line.separator") +
	        "q3 -> q4 [label=0]" + System.getProperty("line.separator") +
	        "q3 -> q0 [label=1]" + System.getProperty("line.separator") +
	        "q4 -> q0 [label=1]" + System.getProperty("line.separator");
	
	public static final String[] NBA_EXAMPLES = 
		{NBA_EXAMPLE_1, NBA_EXAMPLE_2, NBA_EXAMPLE_3};
}
