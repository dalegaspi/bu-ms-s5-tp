package edu.bu.cs622.jlitebox.filter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This is a specialized filtering mechanism for a list of something T This is
 * useful in this application because there is no trivial way to "serialize a
 * java.util.Predicate" without using some kind of eval() or an entirely new DSL
 * to support sophisticated filtering and this mitigates the need for it albeit
 * it would be more limited fashion.
 *
 * @author dlegaspi@bu.edu
 * @param <T> type
 * @param <F> the filter
 */
public class FilteredContentCollection<T, F extends ContentFilter<T>> implements Iterable<T> {
    private final List<T> internalList;

    public FilteredContentCollection(Collection<T> contents) {
        // make the list thread-safe so we are using CopyOnWriteArrayList
        this.internalList = new CopyOnWriteArrayList<>(contents);
    }

    /**
     * filter the list with the specialized filter
     *
     * @param filter the filter object
     * @return the filtered list in the collection
     */
    public Collection<T> filterWith(F filter) {
        return filterWith(filter::test);
    }

    /**
     * This is just a backdoor for filtering directly with a Predicate just added
     * for posterity
     *
     * @param filter the filter predicate
     * @return the filtered list in collection
     */
    public Collection<T> filterWith(Predicate<T> filter) {
        return internalList.stream().filter(filter).collect(Collectors.toList());
    }

    /**
     * convenience method for iteration
     *
     * @return the iterator
     */
    @Override
    public Iterator<T> iterator() {
        return internalList.iterator();
    }
}
