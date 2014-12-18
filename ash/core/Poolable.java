package ash.core;

/**
 * 
 * @author Erik Borgers
 *
 */

public interface Poolable {

	/** clear the Pool-able resources for reuse */
	void release();
}
