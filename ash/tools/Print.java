package ash.tools;

/**
 * 
 * @author Erik Borgers
 *
 */

public abstract class Print {

	public static void NYI() {
		throw new RuntimeException("Not Yet Implemented");
	}

	public static void message(String message) {
		java.lang.System.out.println(message);
	}

	public static void warning(String message) {
		java.lang.System.err.println(message);
	}

	// TODO 7 can we throw something that can be catched, but still print a message? So users could maybe catch engine error instead of crashing?
	public static void fatal(Exception e, String message) {
		java.lang.System.err.println(message);
		e.printStackTrace();
		try {
			throw e;
		} catch (Exception e1) {
			// throw again to make fail
			throw new RuntimeException();
		}
	}

	/** 
	 * prints message, stacktrace and stops the engine
	 * @param message
	 */
	public static void fatal(String message) {
		Exception e = new RuntimeException(message); // create an exception to print the stack trace
		java.lang.System.err.println(message);
		e.printStackTrace();
		try {
			throw e;
		} catch (Exception e1) {
			// throw again to make fail
			throw new RuntimeException();
		}
	}

	/** 
	 * prints message, stacktrace and stops the engine
	 * @param message
	 */
	public static void fatal(String message, Exception e1) {
		java.lang.System.err.println(message);
		e1.printStackTrace();
		throw new RuntimeException(message);
	}
}
