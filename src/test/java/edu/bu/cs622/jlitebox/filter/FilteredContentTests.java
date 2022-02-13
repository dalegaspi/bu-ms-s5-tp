package edu.bu.cs622.jlitebox.filter;

import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Filtered content tests
 *
 * @author dlegaspi@bu.edu
 */
public class FilteredContentTests {
    public static class OverTest implements ContentFilter<Integer> {
        final int than;

        @Override
        public boolean test(Integer item) {
            return item > than;
        }

        OverTest(int than) {
            this.than = than;
        }
    }

    @Test
    public void testBasicGenerics() {
        FilteredContentCollection<Integer, OverTest> c = new FilteredContentCollection<>(List.of(9001, 4, 6, 42, 70));

        var over9000 = c.filterWith(new OverTest(9000));

        assertTrue(over9000.stream().allMatch(i -> i > 9000));
        assertNotNull(c.iterator());
    }

}
