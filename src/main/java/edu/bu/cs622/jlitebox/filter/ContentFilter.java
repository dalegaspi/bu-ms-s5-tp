package edu.bu.cs622.jlitebox.filter;

/**
 * This acts like a java.util.Predicate but in a more focused/limited use case
 * for the purpose of filtering the collection. The key thing here is that we
 * separate how the filtering is done vs how the Filter is implemented.
 *
 * @author dlegaspi@bu.edu
 * @see ContentFilter
 * @param <T> the type
 */
public interface ContentFilter<T> {
    /**
     * Test the item if it matches filter
     *
     * @param item item to test
     * @return true if passes
     */
    boolean test(T item);
}
